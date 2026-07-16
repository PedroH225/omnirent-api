# OmniRent API
## Description
Backend of the OmniRent platform, a marketplace for renting various types of equipment.

The application allows users to list equipment so that other users can search, reserve, and rent items for defined periods.

## Objective
The project was developed as a backend portfolio project focused on production-oriented system architectures and real-world engineering practices.

## Technologies
* **Backend**: Java, Spring Framework (Web, Security), JPA/Hibernate
* **Authentication**: JWT, OAuth2 (Google)
* **Database**: MySQL 8, Query DTOs, optimized queries
* **File Storage**: Cloudflare R2 (Amazon S3-compatible API)
* **Infrastructure**: Docker, Docker Compose, AWS (EC2, EBS, CloudWatch, EventBridge)
* **CI/CD**: GitHub Actions
* **Testing**: JUnit 5, Mockito, Testcontainers, AssertJ
* **Messaging**: RabbitMQ
* **Observability**: Structured logging, SLF4J, Logback
* **Auditing**: Tracking of critical system actions and changes
* **Payments**: Stripe (Sandbox)
* **Localization**: API response internationalization and timezone handling

## Setup
**1.** Configure your environment variables: [Example](.env-example)

**2.** Run:
```bash
docker compose up
```

## Features

### Equipment
- Users can list equipment for rent
- Filter equipment by condition, title, category and subcategories
- Sort by recent, highest price and lowest prices
- Equipment availability management
- Listing information updates
- Upload up to 5 images per item

### Rentals
- Rental requests for defined periods
- Equipment availability validation
- Rental lifecycle management
- Preserved history of rented equipment information

### Payments
- Rental checkout through online payment
- Automatic status updates after payment confirmation
- Payment cancellation and refunds
- Rental renewal through a new payment

### Authentication
- User registration
- Login with email and password
- Login using Google and Github accounts

### System
- Tracking of important actions and changes
- Notification and payment processing
- Automatic updates for overdue rental and payment statuses
- Simulated delivery workflow with automatic status updates after a defined period
