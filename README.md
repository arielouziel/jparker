# JParker Open API

JParker is a superb REST API for Parking Lot Management

- API version: 1.0.0

## Features

- List all parking lots
- Create a new parking lot
- Delete a parking lot
- Add a slot in a parking lot
- Remove a slot from a parking lot
- Enter a parking lot and get a ticket
- Leave a parking lot with ticket and get billed

## Build and run

### Configuration

By default JParker uses an H2 embedded database in ./test.h2.db file which is dropped at each startup.

Feel free to alter this default configuration to feel your needs:  

- Open the `application.properties` file and set your own configurations (mainly database's connection parameters).

### Prerequisites

- Java 8
- Maven > 3.0

### From terminal

Go on the project's root folder, then type:

    $ mvn spring-boot:run

## Usage

- Run the application
- Go on *http://localhost:8080/* and play with the Swagger UI.
- You can also use our [JParker Java Client and CLI](https://github.com/arielouziel/jparker-cli).

## Seed

You can seed the database automatically at [**ParkingLotSeeder.java**](src/main/java/com/aouziel/jparker/database/ParkingLotSeeder.java)

### Example

```
parkingLotRepository.save(ParkingLot.builder()
        .name("My First Parking Lot")
        .pricingPolicy(HourRatePricingPolicy.builder()
                .currencyCode("EUR")
                .hourPrice(150)
                .build()
        )
        .slot(ParkingSlot.builder()
                .type(CarPowerType.twentyKw)
                .location("001")
                .build()
        )
        .slot(ParkingSlot.builder()
                .type(CarPowerType.fiftyKw)
                .location("002")
                .build()
        )
        .slot(ParkingSlot.builder()
                .type(CarPowerType.sedan)
                .location("003")
                .build()
        )
        .slot(ParkingSlot.builder()
                .type(CarPowerType.sedan)
                .location("004")
                .build()
        )
        .build()
);
```

## API Routes

All URIs are relative to *http://localhost:8080*

HTTP request | Description
------------- | -------------
**POST** /api/v1/parking-lots | Create a new parking lot
**POST** /api/v1/parking-lots/{lotId}/slots | Create a new slot in a parking lot
**DELETE** /api/v1/parking-lots/{lotId} | Delete a parking
**POST** /api/v1/parking-lots/{lotId}/tickets | Put a car in a any free parking slot
**GET** /api/v1/parking-lots | View a list of all parking lots
**GET** /api/v1/parking-lots/{lotId} | Get a parking lot by id
**PUT** /api/v1/parking-lots/{lotId}/tickets/{ticketNumber}/leave | Remove car from parking lot and bill the customer
**GET** /api/v1/parking-lots/{lotId}/slots | Get a list of free slots in a parking lot
**DELETE** /api/v1/parking-lots/{lotId}/slots/{slotId} | Remove a slot from a parking lot

## API Documentation

For the whole generated documentation, follow the link

[JParker Java Client and CLI](https://github.com/arielouziel/jparker-cli).