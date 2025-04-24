# CareerConnect

A comprehensive job portal platform built with Spring Boot, connecting job seekers (Candidates) and employers (Recruiters). It offers features for job searching, posting, application management, user profiles, real-time communication, and administrative oversight.

## Overview

CareerConnect aims to streamline the recruitment process by providing a feature-rich environment for both candidates looking for opportunities and recruiters seeking talent. Key functionalities include detailed user profiles, advanced job search filters, an integrated application tracking system, real-time chat between candidates and recruiters, online interview scheduling and signaling, secure payments via VNPay for premium features (like coin recharges), and an AI-powered chatbot for assistance.

## Core Features

*   **Job Management:** Post, search (with filters), apply for, and manage job listings.
*   **User Profiles:** Comprehensive profiles for Candidates (CVs, skills, experience) and Recruiters (company details, team management).
*   **Authentication:** Secure registration and login via credentials or Google OAuth2, with JWT-based API protection.
*   **Real-time Interaction:** Integrated chat between users, plus online interview scheduling and WebRTC signaling.
*   **Candidate Tools:** Save favorite jobs, subscribe to personalized job alerts, and interact with an AI Chatbot.
*   **Recruiter Tools:** Manage company profile, invite team members, track applicants.
*   **Admin Dashboard:** Oversee users, companies, jobs, transactions, and view system statistics.
*   **Monetization:** VNPay integration allows users to purchase credits (coins) for specific actions.
*   **Notifications:** In-app alerts for important events like applications and interviews.
*   **Supporting Tech:** Cloudinary for file storage, RabbitMQ for background tasks, Redis for caching.

## Technologies Used

*   **Backend:** Java 21, Spring Boot 3.4+
*   **Data:** Spring Data JPA, Hibernate, MySQL
*   **Security:** Spring Security, JWT, OAuth2 (Google)
*   **Real-time:** Spring WebSockets, STOMP, SockJS
*   **Messaging:** RabbitMQ
*   **Caching:** Redis (via Spring Data Redis, Jedis)
*   **API:** Spring Web MVC, Spring WebFlux (for WebClient), RESTful APIs
*   **API Docs:** SpringDoc OpenAPI (Swagger UI)
*   **File Storage:** Cloudinary
*   **Payment:** VNPay
*   **AI:** Groq API (LLaMA) for Chatbot
*   **Build:** Maven
*   **Deployment:** Docker, Azure Web Apps, Nixpacks (configuration provided)
*   **Configuration:** Dotenv (`.env` file support)
*   **Libraries:** Lombok, MapStruct, Jackson

## Getting Started

### Prerequisites

*   JDK 21 or later
*   Maven 3.6+
*   MySQL Database Server
*   Redis Server
*   RabbitMQ Server
*   Cloudinary Account (API Key, Secret, Cloud Name)
*   VNPay Merchant Account (TmnCode, HashSecret, URLs)
*   Google OAuth 2.0 Client ID
*   (Optional) Groq AI API Key for chatbot functionality

### Configuration

1.  Create a `.env` file in the root directory of the project.
2.  Add the necessary environment variables. Example:

    ```dotenv
    # Database Configuration
    DB_URL=jdbc:mysql://localhost:3306/careerconnect_db?createDatabaseIfNotExist=true
    DB_USERNAME=your_db_user
    DB_PASSWORD=your_db_password
    # Use the appropriate driver class name if different
    # DB_DRIVER=com.mysql.cj.jdbc.Driver

    # JWT Configuration
    JWT_SECRET=YourVeryStrongAndLongJWTSecretKeyHerePreferablyBase64Encoded
    JWT_EXPIRATION_MS=3600000 # 1 hour
    JWT_REFRESH_EXPIRATION_MS=2592000000 # 30 days

    # Redis Configuration
    REDIS_HOST=localhost
    REDIS_PORT=6379
    REDIS_PASSWORD=your_redis_password # Optional, leave empty if none

    # RabbitMQ Configuration (adjust based on your setup)
    RABBITMQ_HOST=localhost
    RABBITMQ_PORT=5672
    RABBITMQ_USERNAME=guest
    RABBITMQ_PASSWORD=guest
    RABBITMQ_VHOST=/
    CAREERCONNECT_RABBITMQ_TOPIC_EXCHANGE=job_alert_exchange
    CAREERCONNECT_RABBITMQ_DIRECT_EXCHANGE=application_exchange
    CAREERCONNECT_RABBITMQ_QUEUE=job_alert_queue
    CAREERCONNECT_RABBITMQ_APPLICATION_QUEUE=job_application_queue
    CAREERCONNECT_RABBITMQ_ROUTINGKEY=job.alert.#
    CAREERCONNECT_RABBITMQ_APPLICATION_ROUTINGKEY=job.application.notify

    # Cloudinary Configuration
    CLOUDINARY_CLOUD_NAME=your_cloud_name
    CLOUDINARY_API_KEY=your_api_key
    CLOUDINARY_API_SECRET=your_api_secret
    CLOUDINARY_SECURE=true

    # Google OAuth2 Configuration
    GOOGLE_CLIENT_ID=your_google_client_id.apps.googleusercontent.com
    # GOOGLE_CLIENT_SECRET=your_google_client_secret # Usually configured via application properties

    # VNPay Configuration
    VNPAY_PAY_URL=https://sandbox.vnpayment.vn/paymentv2/vpcpay.html # Sandbox URL
    VNPAY_RETURN_URL=http://localhost:8080/api/vnpay/payment-return # Your backend return URL
    VNPAY_TMN_CODE=YOUR_VNPAY_TMNCODE
    VNPAY_HASH_SECRET=YOUR_VNPAY_HASH_SECRET

    # AI Configuration
    AI_GROQ_API_KEY=your_groq_api_key # Optional
    AI_GROQ_ENABLED=true # or false to disable

    # Frontend URL (for redirects, CORS, email links)
    FRONTEND_URL=http://localhost:3000

    # Allowed Origins for CORS
    ALLOWED_ORIGINS=http://localhost:3000,https://your-frontend-domain.com

    # Add any other necessary environment variables...
    ```

3.  Ensure the `spring.datasource.*`, `spring.redis.*`, `spring.rabbitmq.*`, `spring.security.oauth2.client.registration.google.*` properties in `application.properties` or `application.yml` (if used) either reference these environment variables or are correctly set. *Note: The current setup primarily uses `.env` loaded at startup.*

### Running the Application

1.  Make sure your MySQL, Redis, and RabbitMQ servers are running.
2.  Navigate to the project's root directory.
3.  Run the application using Maven:
    ```bash
    ./mvnw spring-boot:run
    # or on Windows:
    # mvnw.cmd spring-boot:run
    ```
4.  The application should start on `http://localhost:8080` (or the configured port).


## API Documentation

Once the application is running, API documentation is available via Swagger UI at:

`http://localhost:8080/swagger-ui.html`

## Deployment

*   **Docker:** A `Dockerfile` is provided for building a container image.
*   **Nixpacks:** A `nixpacks.toml` file is included for building with Nixpacks (often used by platforms like Railway).
*   **Azure:** A GitHub Actions workflow (`.github/workflows/a_careerconnect.yml`) is configured for deploying the application to Azure Web Apps.

## Contributing

Contributions are welcome! If you'd like to contribute, please follow these general guidelines:

1.  **Fork the repository.**
2.  **Create a new branch** for your feature or bug fix (e.g., `feature/add-new-filter` or `fix/login-issue`).
3.  **Make your changes.** Ensure code follows existing style conventions.
4.  **Write tests** for your changes if applicable.
5.  **Commit your changes** with clear and descriptive messages.
6.  **Push your branch** to your fork.
7.  **Open a Pull Request** to the `main` branch of the original repository. Describe your changes and why they are needed.

## License

This project is currently unlicensed.

*(Optional: Replace the line above with your chosen license, e.g., "This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details." and add a `LICENSE` file to your repository.)*
