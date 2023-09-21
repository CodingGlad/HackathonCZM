package cz.cvut.fel.camunda.workshops.developer.handlers;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@ExternalTaskSubscription(
        topicName = "handleReservation"
)
public class ReservationHandler implements ExternalTaskHandler {
    @Autowired
    PostgresHandler postgresHandler;
    @Autowired
    SendgridWrapper sendgridWrapper;

    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {

        Map<String, Object> inputVariables = externalTask.getAllVariables();
        log.info(inputVariables.toString());

        String carsInsert = """
                INSERT INTO Cars (spz, manufacturer, model)
                VALUES (?, ?, ?)
                ON CONFLICT (spz)
                DO NOTHING
                """;
        try {
            PreparedStatement carsPreparedStatement = postgresHandler.getConnection().prepareStatement(carsInsert);
            carsPreparedStatement.setString(1, (String) inputVariables.get("spz"));
            carsPreparedStatement.setString(2, (String) inputVariables.get("car_manufacturer"));
            carsPreparedStatement.setString(3, (String) inputVariables.get("model"));
            carsPreparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("There was a problem adding your car to out database");
        }

        String driverInsert = """
                INSERT INTO Drivers (telephone, name, surname, email)
                VALUES (?, ?, ?, ?)
                ON CONFLICT (telephone)
                DO UPDATE SET email = excluded.email;
                """;
        try {
            PreparedStatement carsPreparedStatement = postgresHandler.getConnection().prepareStatement(driverInsert);
            carsPreparedStatement.setString(1, (String) inputVariables.get("phone"));
            carsPreparedStatement.setString(2, (String) inputVariables.get("name"));
            carsPreparedStatement.setString(3, (String) inputVariables.get("surname"));
            carsPreparedStatement.setString(4, (String) inputVariables.get("mail"));
            carsPreparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("There was a problem adding your driver profile to out database");
        }


        if ("need".equals(inputVariables.get("radio"))) {
            String tireInsert = """
                    INSERT INTO Tires (code, spz_of_car, size_of_tires
                     tire_manufacturer, is_stored, season_type)
                    VALUES (?, ?, ?, ?, ?, ?);
                    """;
            try {
                PreparedStatement tirePreparedStatement = postgresHandler.getConnection().prepareStatement(tireInsert);
                tirePreparedStatement.setString(1, String.valueOf(UUID.randomUUID()));
                tirePreparedStatement.setString(2, (String) inputVariables.get("spz"));
                tirePreparedStatement.setString(3, (String) inputVariables.get("tire_size"));
                tirePreparedStatement.setString(4, (String) ((inputVariables.get("tire_manufacturer") != null)
                        ? inputVariables.get("tire_manufacturer") : null));
                tirePreparedStatement.setBoolean(5, false);
                tirePreparedStatement.setString(6, (String) inputVariables.get("season"));
                tirePreparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("There was a problem adding your tires profile to out database");
            }
        }

        UUID reservationId = UUID.randomUUID();
        String reservationInsert = """
                INSERT INTO Reservation (number_of_reservation, date_of_reservtion, telephone_driver, spz_of_car)
                VALUES (?, ?, ?, ?);
                    """;
        try {
            PreparedStatement reservationPreparedStatement = postgresHandler.getConnection().prepareStatement(reservationInsert);
            reservationPreparedStatement.setString(1, String.valueOf(reservationId));
            reservationPreparedStatement.setTimestamp(2, Timestamp
                    .valueOf(convertToSQLTimestamp((String) inputVariables.get("date"))));
            reservationPreparedStatement.setString(3, (String) inputVariables.get("phone"));
            reservationPreparedStatement.setString(4, (String) inputVariables.get("spz"));
        } catch (SQLException e) {
            throw new RuntimeException("There was a problem adding your reservation profile to out database");
        }


        log.info("successfully created a reservation " + reservationId);


        try {
            sendgridWrapper.sendMail((String) inputVariables.get("mail"),
                    "USPESNE VYTVORENA OBJEDNAVKA " + reservationId);
            log.info("Send notification to " + inputVariables.get("mail"));
        } catch (IOException e) {
            throw new RuntimeException("ERROR SENDING NOTIFICATION",e);
        }
        externalTaskService.complete(externalTask);
    }

    private String convertToSQLTimestamp (String inputDate) {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            // Parse the input string into a Date object
            Date date = inputDateFormat.parse(inputDate);

            // Format the Date object into the PostgreSQL-compatible timestamp format
            return outputDateFormat.format(date);
        } catch (ParseException e) {
            throw new RuntimeException("There was an issue creating your reservation");
        }
    }
}
