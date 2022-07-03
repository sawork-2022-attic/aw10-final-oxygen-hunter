package com.micropos.delivery.rest;

import com.micropos.api.DeliveryApi;
import com.micropos.delivery.mapper.DeliveryMapper;
import com.micropos.delivery.model.Entry;
import com.micropos.delivery.repository.DeliveryRepository;
import com.micropos.delivery.service.DeliveryService;
import com.micropos.delivery.service.MessageListener;
import com.micropos.delivery.service.MessagePublisher;
import com.micropos.dto.DeliveryEntryDto;
import com.micropos.dto.OrderDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static java.time.Duration.ofSeconds;

@RestController
@RequestMapping("api")
public class DeliveryController implements DeliveryApi {

    private DeliveryMapper deliveryMapper;

    private DeliveryService deliveryService;

    private DeliveryRepository deliveryRepository;

    private static final Logger log = LoggerFactory.getLogger(DeliveryController.class);

    private final TaskScheduler scheduler = new ConcurrentTaskScheduler();

    private static final long DELIVERING_DELAY = 20;

    private static final long DELIVERED_DELAY = 20;

    private MessagePublisher publisher;

    private final ConnectableFlux<Entry> messageFlux;


    @Autowired
    public void setDeliveryMapper(DeliveryMapper deliveryMapper) { this.deliveryMapper = deliveryMapper; }

    @Autowired
    public void setDeliveryService(DeliveryService deliveryService) { this.deliveryService = deliveryService; }

    @Autowired
    public void setDeliveryRepository(DeliveryRepository deliveryRepository) { this.deliveryRepository = deliveryRepository; }

    @Autowired
    public void setPublisher(MessagePublisher publisher) { this.publisher = publisher; }

    @Autowired
    public DeliveryController() {
        messageFlux = Flux.<Entry>create(sink -> {
            publisher.subscribe(new MessageListener() {
                @Override
                public void onNext(Entry entry) {
                    //log.info("on next: {}", entry.toString());
                    sink.next(entry);
                }

                @Override
                public void onComplete() {
                    sink.complete();
                }
            });
        }).publish();
        //messageFlux.subscribe(entry -> System.out.println(entry.toString()));
        messageFlux.connect();
    }

    @Override
    public Mono<ResponseEntity<Flux<DeliveryEntryDto>>> listDelivery(ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok(deliveryService.getAllEntries().map(deliveryMapper::toEntryDto)));
    }

    @GetMapping("/query")
    public Flux<ServerSentEvent<String>> streamEvents(String orderId) {
        return deliveryService.getEntryByOrderId(orderId)
                .map(Entry::toString)
                .flatMapMany(entryStr -> Flux.interval(Duration.ofSeconds(1))
                        .map(sequence -> ServerSentEvent.<String>builder()
                                .id(String.valueOf(sequence))
                                .event("periodic-event")
                                .data(entryStr)
                                .build()));
    }


//    @GetMapping("/query")
//    public Flux<ServerSentEvent<String>> streamEvents() {
//        return Flux.from(messageFlux).map(entry -> ServerSentEvent.<String>builder()
//                .id(UUID.randomUUID().toString())
//                .event("Delivery Status Change")
//                .data(entry.toString())
//                .build());
//    }

//    @GetMapping("/sse-emitter")
//    public SseEmitter sseEmitter() {
//        SseEmitter emitter = new SseEmitter();
//        Executors.newSingleThreadExecutor().execute(() -> {
//            try {
//                for (int i = 0; true; i++) {
//                    SseEmitter.SseEventBuilder event = SseEmitter.event()
//                            .id(String.valueOf(i))
//                            .name("SSE_EMITTER_EVENT")
//                            .data("SSE EMITTER - " + LocalTime.now().toString());
//                    emitter.send(event);
//                    Thread.sleep(1000);
//                }
//            } catch (Exception ex) {
//                emitter.completeWithError(ex);
//            }
//        });
//        return emitter;
//    }

    @Bean
    Consumer<OrderDto> receiveOrder() {
//        return orderDto -> {
//            log.info("receive: {}",  orderDto);
//            createEntry(orderDto);
//        };
        return orderDto -> deliveryRepository.save(new Entry().orderId(orderDto.getId()).status("preparing")).subscribe(entry -> {
            publisher.publish(entry);

            scheduler.schedule(() -> deliveryRepository.findById(entry.id())
                    .flatMap(entry2 -> {
                        entry2.status("delivering");
                        return deliveryRepository.save(entry2);
                    })
                    .subscribe(entry2 -> publisher.publish(entry2)), LocalDateTime.now().plusSeconds(DELIVERING_DELAY).
                    atZone(TimeZone.getDefault().toZoneId()).toInstant());

            scheduler.schedule(() -> deliveryRepository.findById(entry.id())
                    .flatMap(entry2 -> {
                        entry2.status("delivered");
                        return deliveryRepository.save(entry2);
                    })
                    .subscribe(entry2 -> publisher.publish(entry2)), LocalDateTime.now().plusSeconds(DELIVERING_DELAY+DELIVERED_DELAY).
                    atZone(TimeZone.getDefault().toZoneId()).toInstant());
        });
    }

    private void createEntry(OrderDto orderDto) {
        Entry entry = new Entry()
                .orderId(orderDto.getId())
                .status("preparing");
        deliveryRepository.save(entry).subscribe(entry1 -> {
            publisher.publish(entry1);

            scheduler.schedule(() -> deliveryRepository.findById(entry1.id())
                    .flatMap(entry2 -> {
                        entry2.status("delivering");
                        return deliveryRepository.save(entry2);
                    })
                    .subscribe(entry2 -> publisher.publish(entry2)), LocalDateTime.now().plusSeconds(DELIVERING_DELAY).
                    atZone(TimeZone.getDefault().toZoneId()).toInstant());

            scheduler.schedule(() -> deliveryRepository.findById(entry1.id())
                    .flatMap(entry2 -> {
                        entry2.status("delivered");
                        return deliveryRepository.save(entry2);
                    })
                    .subscribe(entry2 -> publisher.publish(entry2)), LocalDateTime.now().plusSeconds(DELIVERING_DELAY+DELIVERED_DELAY).
                    atZone(TimeZone.getDefault().toZoneId()).toInstant());
        });
    }
}
