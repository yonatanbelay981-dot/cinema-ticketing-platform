package com.cinema.tiecket_service.service;

import com.cinema.tiecket_service.dto.TicketResponse;
import com.cinema.tiecket_service.entity.Ticket;
import com.cinema.tiecket_service.entity.TicketStatus;
import com.cinema.tiecket_service.event.PaymentProcessedEvent;
import com.cinema.tiecket_service.exception.TicketNotFoundException;
import com.cinema.tiecket_service.repository.TicketRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class TicketServiceImplementation implements TicketService{
    private  final TicketRepository ticketRepository;

    public TicketServiceImplementation(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public Page<TicketResponse> getAllTickets(Pageable pageable) {
        log.info("Fetching all tickets");
        Page<Ticket> ticketPage = ticketRepository.findAll(pageable);
        log.info("Successfully fetched {} tickets", ticketPage.getNumberOfElements());
        return ticketPage.map(this::mapTOTIcketResponse);
    }

    @Override
    public TicketResponse getTicketById(UUID id ,  String  keycloakUserId ) {
        log.info("Fetching ticket with id user id {}", id);

        Ticket ticket = ticketRepository.findByIdAndKeycloakUserId(id , keycloakUserId)
                .orElseThrow(() -> {
                    log.warn("while fetching Ticket with id  {}  and keycloak userId {} was not found", id , keycloakUserId);
                    return new TicketNotFoundException("Ticket with  id " + id + " and userId "+keycloakUserId+" was not found");
                });
        log.info("Ticket with id  {} found successfully", id);
        return mapTOTIcketResponse(ticket);
    }


    @Override
    public TicketResponse getTicketByBookingId(UUID bookingId ,  String keycloakUserId) {
        log.info(
                "Fetching ticket with booking id {} for user {}",
                bookingId,
                keycloakUserId
        );

        Ticket ticket = ticketRepository
                .findByBookingIdAndKeycloakUserId(
                        bookingId,
                        keycloakUserId
                )
                .orElseThrow(() -> {
                    log.warn(
                            "Ticket with booking id {} for user {} was not found",
                            bookingId,
                            keycloakUserId
                    );

                    return new TicketNotFoundException(
                            "Ticket not found"
                    );
                });

        return mapTOTIcketResponse(ticket);
    }

    @Override
    public void cancelTicket(UUID ticketId ,  String keycloakUserId) {
        log.info("Cancelling ticket with id {}", ticketId);
        Ticket ticket = ticketRepository
                .findByIdAndKeycloakUserId(ticketId, keycloakUserId)
                .orElseThrow(() -> {
                    log.warn(
                            "Ticket {} does not belong to user {}",
                            ticketId,
                            keycloakUserId
                    );

                    return new TicketNotFoundException(
                            "Ticket not found"
                    );
                });
        if (ticket.getStatus() != TicketStatus.ACTIVE) {
            log.warn(
                    "Ticket {} cannot be cancelled because its status is {}",
                    ticketId,
                    ticket.getStatus()
            );
            return;
        }
        ticket.setStatus(TicketStatus.CANCELLED);
        ticketRepository.save(ticket);
        log.info("Ticket with id {} cancelled successfully", ticketId);

    }
    @Override
    public TicketResponse useTicket(UUID ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() ->
                        new TicketNotFoundException(
                                "Ticket with id " + ticketId + " was not found"
                        )
                );

        if (ticket.getStatus() != TicketStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Ticket cannot be used because its status is "
                            + ticket.getStatus()
            );
        }

        ticket.setStatus(TicketStatus.USED);

        Ticket savedTicket = ticketRepository.save(ticket);

        log.info("Ticket {} marked as USED", ticketId);

        return mapTOTIcketResponse(savedTicket);
    }
    @Override
    public void createTicketFromPayment(PaymentProcessedEvent event) {

        if (!"SUCCESS".equals(event.status())) {
            log.info(
                    "Payment for booking {} was not successful. No ticket will be created.",
                    event.bookingId()
            );
            return;
        }

        Optional<Ticket> existingTicket =
                ticketRepository.findByBookingId(event.bookingId());

        if (existingTicket.isPresent()) {

            log.warn(
                    "Ticket for booking {} already exists",
                    event.bookingId()
            );

            return;
        }

        Ticket ticket = new Ticket();
        ticket.setKeycloakUserId(event.keycloakUserId());
        ticket.setBookingId(event.bookingId());
        ticket.setQrCode(UUID.randomUUID().toString());
        ticket.setStatus(TicketStatus.ACTIVE);

        Ticket savedTicket = ticketRepository.save(ticket);

        log.info(
                "Ticket {} created for booking {}",
                savedTicket.getId(),
                savedTicket.getBookingId()
        );


    }
    @Override
    public byte[] generateTicketQrCode(UUID ticketId , String keycloakUserId) {
        Ticket ticket = ticketRepository
                .findByIdAndKeycloakUserId(ticketId, keycloakUserId)
                .orElseThrow(() -> {
                    log.warn(
                            "Ticket {} does not belong to user {}",
                            ticketId,
                            keycloakUserId
                    );

                    return new TicketNotFoundException(
                            "Ticket not found"
                    );
                });
        try {
            return generateQrCode(ticket.getQrCode(), 300, 300);
        } catch (WriterException | IOException e) {
            log.error("Error generating QR code for ticket {}", ticketId, e);
            throw new RuntimeException("Error generating QR code", e);
        }
    }

    @Override
    public TicketResponse getByQrcode(String qrCode) {

        Ticket ticket = ticketRepository.findByQrCode(qrCode)
                .orElseThrow(() ->
                        new TicketNotFoundException(
                                "Ticket with QR code " + qrCode + " was not found"
                        )
                );

        if (ticket.getStatus() != TicketStatus.ACTIVE) {

            log.warn(
                    "Ticket with QR code {} cannot be used because its status is {}",
                    qrCode,
                    ticket.getStatus()
            );

            throw new IllegalStateException(
                    "Ticket cannot be used because its status is "
                            + ticket.getStatus()
            );
        }

       TicketResponse ticket1 = useTicket(ticket.getId());

        log.info(
                "Ticket {} was successfully validated and marked as USED",
                ticket1.getId()
        );

        return ticket1;
    }


    @Override
    public Page<TicketResponse> getMyTickets(String keycloakUserId, Pageable pageable) {
        return ticketRepository
                .findByKeycloakUserId(keycloakUserId, pageable)
                .map(this::mapTOTIcketResponse);
    }


    private byte[] generateQrCode(String text  , int width  ,   int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text ,  BarcodeFormat.QR_CODE , width , height);
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix , "PNG" , pngOutputStream , new MatrixToImageConfig());
        return pngOutputStream.toByteArray();
    }

    private TicketResponse mapTOTIcketResponse(Ticket ticket){
       return  new TicketResponse(
               ticket.getId(),
               ticket.getBookingId(),
               ticket.getQrCode(),
               ticket.getStatus(),
               ticket.getGeneratedAt()
       );
    }
}
