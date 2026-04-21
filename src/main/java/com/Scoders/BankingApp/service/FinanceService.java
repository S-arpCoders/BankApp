package com.Scoders.BankingApp.service;

import com.Scoders.BankingApp.model.MarketData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FinanceService {

    @Value("${finance.alpha.vantage.api.key:}")
    private String alphaVantageApiKey;

    @Value("${finance.use.mock.data:true}")
    private boolean useMockData;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ConcurrentHashMap<String, MarketData> cachedData = new ConcurrentHashMap<>();
    private LocalDateTime lastUpdateTime = null;

    private static final String[] STOCK_SYMBOLS = {"AAPL", "GOOGL", "MSFT", "TSLA", "AMZN"};
    private static final String[] STOCK_NAMES = {"苹果公司", "谷歌", "微软", "特斯拉", "亚马逊"};
    
    private static final String[] FUTURE_SYMBOLS = {"GC", "SI", "CL", "NG", "HG"};
    private static final String[] FUTURE_NAMES = {"黄金期货", "白银期货", "原油期货", "天然气期货", "铜期货"};

    @PostConstruct
    public void init() {
        refreshMarketData();
    }

    public List<MarketData> getAllMarketData() {
        if (needsRefresh()) {
            refreshMarketData();
        }
        return new ArrayList<>(cachedData.values());
    }

    public List<MarketData> getStockData() {
        return getMarketDataByType("stock");
    }

    public List<MarketData> getGoldData() {
        return getMarketDataByType("gold");
    }

    public List<MarketData> getFutureData() {
        return getMarketDataByType("future");
    }

    private List<MarketData> getMarketDataByType(String type) {
        if (needsRefresh()) {
            refreshMarketData();
        }
        List<MarketData> result = new ArrayList<>();
        for (MarketData data : cachedData.values()) {
            if (type.equals(data.getType())) {
                result.add(data);
            }
        }
        return result;
    }

    private boolean needsRefresh() {
        if (lastUpdateTime == null) {
            return true;
        }
        return LocalDateTime.now().minusHours(1).isAfter(lastUpdateTime);
    }

    public void refreshMarketData() {
        if (useMockData || alphaVantageApiKey == null || alphaVantageApiKey.isEmpty()) {
            generateMockData();
        } else {
            fetchRealData();
        }
        lastUpdateTime = LocalDateTime.now();
    }

    private void generateMockData() {
        Random random = new Random();

        for (int i = 0; i < STOCK_SYMBOLS.length; i++) {
            String symbol = STOCK_SYMBOLS[i];
            String name = STOCK_NAMES[i];
            
            double basePrice = getBasePrice(symbol);
            double open = basePrice + (random.nextDouble() - 0.5) * basePrice * 0.02;
            double close = open + (random.nextDouble() - 0.5) * basePrice * 0.03;
            double high = Math.max(open, close) + random.nextDouble() * basePrice * 0.01;
            double low = Math.min(open, close) - random.nextDouble() * basePrice * 0.01;
            double change = close - open;
            double changePercent = (change / open) * 100;

            MarketData data = new MarketData(symbol, name, "stock", close, change, changePercent, high, low, open);
            cachedData.put(symbol, data);
        }

        MarketData goldData = createMockGoldData(random);
        cachedData.put("XAU", goldData);

        for (int i = 0; i < FUTURE_SYMBOLS.length; i++) {
            String symbol = FUTURE_SYMBOLS[i];
            String name = FUTURE_NAMES[i];
            
            if ("GC".equals(symbol)) {
                continue;
            }

            double basePrice = getFutureBasePrice(symbol);
            double open = basePrice + (random.nextDouble() - 0.5) * basePrice * 0.015;
            double close = open + (random.nextDouble() - 0.5) * basePrice * 0.02;
            double high = Math.max(open, close) + random.nextDouble() * basePrice * 0.008;
            double low = Math.min(open, close) - random.nextDouble() * basePrice * 0.008;
            double change = close - open;
            double changePercent = (change / open) * 100;

            MarketData data = new MarketData(symbol, name, "future", close, change, changePercent, high, low, open);
            cachedData.put(symbol, data);
        }
    }

    private MarketData createMockGoldData(Random random) {
        double basePrice = 2350.00;
        double open = basePrice + (random.nextDouble() - 0.5) * 20;
        double close = open + (random.nextDouble() - 0.5) * 15;
        double high = Math.max(open, close) + random.nextDouble() * 5;
        double low = Math.min(open, close) - random.nextDouble() * 5;
        double change = close - open;
        double changePercent = (change / open) * 100;

        return new MarketData("XAU", "黄金现货", "gold", close, change, changePercent, high, low, open);
    }

    private double getBasePrice(String symbol) {
        return switch (symbol) {
            case "AAPL" -> 175.00;
            case "GOOGL" -> 140.00;
            case "MSFT" -> 400.00;
            case "TSLA" -> 170.00;
            case "AMZN" -> 180.00;
            default -> 100.00;
        };
    }

    private double getFutureBasePrice(String symbol) {
        return switch (symbol) {
            case "SI" -> 28.50;
            case "CL" -> 80.00;
            case "NG" -> 2.50;
            case "HG" -> 4.20;
            default -> 100.00;
        };
    }

    private void fetchRealData() {
        for (int i = 0; i < STOCK_SYMBOLS.length; i++) {
            try {
                MarketData data = fetchStockData(STOCK_SYMBOLS[i], STOCK_NAMES[i]);
                if (data != null) {
                    cachedData.put(STOCK_SYMBOLS[i], data);
                }
            } catch (Exception e) {
                System.err.println("获取股票数据失败: " + STOCK_SYMBOLS[i]);
            }
        }

        try {
            MarketData goldData = fetchCommodityData("XAU", "黄金现货");
            if (goldData != null) {
                cachedData.put("XAU", goldData);
            }
        } catch (Exception e) {
            System.err.println("获取黄金数据失败");
        }

        for (int i = 0; i < FUTURE_SYMBOLS.length; i++) {
            if ("GC".equals(FUTURE_SYMBOLS[i])) {
                continue;
            }
            try {
                MarketData data = fetchFutureData(FUTURE_SYMBOLS[i], FUTURE_NAMES[i]);
                if (data != null) {
                    cachedData.put(FUTURE_SYMBOLS[i], data);
                }
            } catch (Exception e) {
                System.err.println("获取期货数据失败: " + FUTURE_SYMBOLS[i]);
            }
        }

        if (cachedData.isEmpty()) {
            generateMockData();
        }
    }

    private MarketData fetchStockData(String symbol, String name) {
        try {
            String url = String.format(
                "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=%s&apikey=%s",
                symbol, alphaVantageApiKey
            );
            
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode quote = root.path("Global Quote");
            
            if (quote.isMissingNode() || quote.isEmpty()) {
                return null;
            }

            double price = quote.path("05. price").asDouble();
            double open = quote.path("02. open").asDouble();
            double high = quote.path("03. high").asDouble();
            double low = quote.path("04. low").asDouble();
            double change = quote.path("09. change").asDouble();
            String changePercentStr = quote.path("10. change percent").asText();
            double changePercent = Double.parseDouble(changePercentStr.replace("%", ""));

            return new MarketData(symbol, name, "stock", price, change, changePercent, high, low, open);
        } catch (Exception e) {
            System.err.println("获取股票数据异常: " + e.getMessage());
            return null;
        }
    }

    private MarketData fetchCommodityData(String symbol, String name) {
        try {
            String function = "ALUMINUM";
            if ("XAU".equals(symbol)) {
                function = "WTI";
            }
            
            String url = String.format(
                "https://www.alphavantage.co/query?function=%s&apikey=%s",
                function, alphaVantageApiKey
            );
            
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode data = root.path("data");
            
            if (data.isMissingNode() || !data.isArray() || data.size() == 0) {
                return null;
            }

            JsonNode latest = data.get(0);
            double price = latest.path("value").asDouble();
            
            Random random = new Random();
            double change = (random.nextDouble() - 0.5) * price * 0.02;
            double changePercent = (change / price) * 100;
            double high = price + random.nextDouble() * price * 0.01;
            double low = price - random.nextDouble() * price * 0.01;
            double open = price - change;

            return new MarketData(symbol, name, "gold", price, change, changePercent, high, low, open);
        } catch (Exception e) {
            System.err.println("获取大宗商品数据异常: " + e.getMessage());
            return null;
        }
    }

    private MarketData fetchFutureData(String symbol, String name) {
        try {
            String function = switch (symbol) {
                case "SI" -> "SILVER";
                case "CL" -> "WTI";
                case "NG" -> "NATURAL_GAS";
                case "HG" -> "COPPER";
                default -> "WTI";
            };
            
            String url = String.format(
                "https://www.alphavantage.co/query?function=%s&apikey=%s",
                function, alphaVantageApiKey
            );
            
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode data = root.path("data");
            
            if (data.isMissingNode() || !data.isArray() || data.size() == 0) {
                return null;
            }

            JsonNode latest = data.get(0);
            double price = latest.path("value").asDouble();
            
            Random random = new Random();
            double change = (random.nextDouble() - 0.5) * price * 0.015;
            double changePercent = (change / price) * 100;
            double high = price + random.nextDouble() * price * 0.008;
            double low = price - random.nextDouble() * price * 0.008;
            double open = price - change;

            return new MarketData(symbol, name, "future", price, change, changePercent, high, low, open);
        } catch (Exception e) {
            System.err.println("获取期货数据异常: " + e.getMessage());
            return null;
        }
    }

    public LocalDateTime getLastUpdateTime() {
        return lastUpdateTime;
    }

    public boolean isUsingMockData() {
        return useMockData || alphaVantageApiKey == null || alphaVantageApiKey.isEmpty();
    }
}
