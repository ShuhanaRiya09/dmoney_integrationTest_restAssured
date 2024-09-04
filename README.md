# "Dmoney API Automation with REST Assured"

## Project Overview
This project automates a series of financial transactions using the Dmoney API. The tasks include creating new users (customers and agents), performing money transfers, deposits, withdrawals, and payments, as well as verifying account balances. The automation is implemented using REST Assured and Java.

## Prerequisites
- **Java 11** or above
- **Gradle** for dependency management
- **Selenium** for automation tool
- **Selenium** for automation framework (using POM)
- **IDE** like IntelliJ IDEA or Eclipse
- **Git** for version control

### How to Run This Project

1. **Clone the Project:**
   ```bash
   git clone https://github.com/your-github-username/dmoney-api-automation.git
   cd dmoney-api-automation
2. **Open in IntelliJ IDEA:**
   
3. **Build and Test:**   
   ```bash
   gradle clean test

4. **Generate Allure Report:**
   ```bash
   allure generate allure-results --clean -o allure-report

5. **View Allure Report:**
   ```bash
   allure serve allure-results

## Test Scenarios:
- Login as Admin: Authenticates as an admin and stores the token for further API requests.
- Create Users: Creates two new customers and one agent.
- Transfer Money: Transfers 2000 Tk from the system account to the newly created agent.
- Deposit: Deposits 1500 Tk from the agent's account to a customer's account.
- Withdraw: Withdraws 500 Tk from the customer back to the agent.
- Send Money: Transfers 500 Tk from one customer to another.
- Make Payment: Processes a payment of 100 Tk to a specified merchant.
- Check Balance: Verifies the balance of the recipient customer after all transactions.
   
## Automation Showcasing:

## Allure Report:

