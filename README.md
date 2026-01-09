# 📦 Blockchain Shipment Management Tracking System

## 📖 Overview

The **Blockchain Shipment Management Tracking System** is a Java-based platform designed to improve transparency, security, and traceability in supply chain and logistics workflows. Traditional shipment tracking systems often suffer from limited visibility, data manipulation risks, and fragmented records. This system addresses those challenges by simulating a **permissioned blockchain network** that records shipment events immutably and provides stakeholders with a shared, trusted view of shipment data.

The platform supports end-to-end shipment lifecycle management, including shipment creation, document verification, status updates, delivery confirmation, dispute handling, and audit reporting. It was developed as part of a large-scale systems design and implementation project, emphasizing strong software architecture, blockchain concepts, and UML-driven development.

---

## 🚀 Features

- Secure creation and tracking of shipment records  
- Immutable logging of shipment events (pickup, in-transit, delivery, customs clearance)  
- Smart contract–style validation for shipment transitions  
- Off-chain document storage with on-chain hash verification  
- Role-based access for stakeholders (shipper, logistics provider, warehouse, customer, auditor, admin)  
- Real-time shipment status querying and full shipment history review  
- Dispute logging and automated workflow handling  
- Audit trail and compliance report generation  
- Java Swing graphical interface for system interaction  
- Unit testing of core modules using JUnit  

---

## 🏗️ System Architecture

The system follows a modular, layered architecture:

- **Domain Layer:** Core entities such as User, Shipment, Event, Document, and SmartContract  
- **Controller Layer:** Coordinates business logic and shipment lifecycles (e.g., validation, compliance, delivery confirmation)  
- **Gateway / Adapter Layer:** Interfaces for blockchain ledger simulation, off-chain storage, and payment services  
- **Presentation Layer:** Java Swing GUI for stakeholder interaction  

Design patterns were used to separate concerns between UI components, controllers, and backend services, ensuring maintainability and scalability. All core interactions follow the system’s UML class, sequence, and activity diagrams.

---

## 🛠️ Technologies Used

- Java  
- Java Swing  
- Custom blockchain-inspired ledger  
- Smart contract logic  
- Off-chain storage modules  
- JUnit (unit testing)  
- UML system design models  

---

## 🧪 Testing

The system includes a structured test plan and unit tests built with **JUnit** to validate:

- Shipment lifecycle controllers  
- Smart contract rule enforcement  
- Event logging and immutability  
- Dispute handling and audit workflows  

Testing focused on ensuring correctness, traceability, and system reliability.

---

## 📂 Project Documentation

A full technical report is included in this repository and covers:

- Requirements analysis (functional & non-functional)  
- System objectives and major actors  
- UML diagrams (use case, class, sequence, activity)  
- Architecture and design patterns  
- Implementation details  
- Test plans and sample results  

📄 **Project Report:**  
`Blockchain Shipment Management Tracking System.pdf`

---

## ⚠️ Disclaimer

This project is a **blockchain-inspired simulation** built for educational and architectural demonstration purposes. It models key blockchain concepts such as immutability, smart contracts, and distributed trust, but it is not deployed on a public blockchain network.

