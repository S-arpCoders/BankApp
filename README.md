# Account Management Web App

A secure and user-friendly account management application developed using Java, Spring Boot, Thymeleaf, Bootstrap, Sqlite and Docker. This app allows users to create accounts, manage transactions, and track account activities. It also supports multiple users and session handling using cookies.

## 🤝 Our Team  
We are six developers with unique strengths and shared goals.  


| Name                          | Role                 | GitHub Profile                                         |  
|-------------------------------|----------------------|-------------------------------------------------------|  
| **Hlanganani Msawekho**       | Java Developer      | [@Msawekho](https://github.com/Msawekho)             |  
| **Valerie Tshuma**            | Java Developer       | [@Valerietshuma](https://github.com/Valerietshuma)   |  
| **Mulamuleli Mammba**         | Java Developer       | [@MulamuMammba](https://github.com/MulamuMammba)     |  
| **Ntlahla Pikwa**             | Java Developer           | [@Ntlahla80](https://github.com/Ntlahla80)           |  
| **Tshwaraganang Clement Maimane** | Java Developer       | [@ClementRep](https://github.com/ClementRep)         |  
| **Simphiwe Mbatha**           | Java Developer         |  [@MbathaSimphiwe](https://github.com/MbathaSimphiwe) |  

---
## Features

- **Authentication**: Secure login with username and password.
- **Account Management**: Create multiple accounts for users.
- **Transactions**: 
  - Deposit funds into an account.
  - Withdraw funds from an account.
  - Transfer funds between accounts.
  - View all transactions related to a specific account.
- **Multi-user Support**: The app can handle multiple users, allowing them to manage their own accounts.
- **Session Handling**: Uses cookies for session management to keep users logged in across requests.

## Tech Stack

- **Backend**: Java, Spring Boot
- **Frontend**: Thymeleaf, Bootstrap
- **Database**: SQLite
- **Containerization**: Docker
- **Deployment**: Render

## Prerequisites

Before running the application locally, ensure you have the following installed:

- Java 23
- Docker (for containerization)
- Maven (for dependency management)
- SQLite (database)
- An active account on Render (for deployment)

## Installation

### 1. Clone the repository

```bash
git clone https://github.com/S-arpCoders/BankApp.git
cd BankApp
```

### 2. Build the application

Using Maven:

```bash
mvn clean install
```

### 3. Configure SQLite Database

SQLite is used as the database for this app. Ensure that the database file is created and configured correctly.

The app may automatically create it when it runs for the first time.

### 4. Run the application locally

To run the app locally without Docker, use:

```bash
mvn spring-boot:run
```

### 5. Docker Setup

If you prefer to run the application using Docker, follow these steps:

- Build the Docker image:

```bash
docker build -t BankApp .
```

- Run the Docker container:

```bash
docker run -p 8080:8080 BankApp
```

### 6. Access the application

Once the app is running, access it in your browser at:

```
http://localhost:8080
```

## Features in Detail

### Authentication

- Users can register with a unique username and password.
- The app uses Http Session to handle authentication, with session management through cookies.

### Account Management

- Multiple accounts can be created for each user.
- Each account can hold a balance, and transactions can be performed between accounts.

### Transactions

- **Deposit**: Add funds to an account.
- **Withdraw**: Remove funds from an account.
- **Transfer**: Transfer funds between accounts.
- **Transaction History**: View all transactions associated with a particular account.

### Session Handling

- The application uses cookies to maintain user sessions and prevent users from needing to log in repeatedly.

## Deployment

This app is deployed on Render. Once deployed, users can access the app from the Render URL:

```
https://bankapp-scoder.onrender.com
```

## Contributing

This project was developed by a team of 6 developers. If you'd like to contribute, feel free to fork the repository, make improvements, and submit pull requests.
