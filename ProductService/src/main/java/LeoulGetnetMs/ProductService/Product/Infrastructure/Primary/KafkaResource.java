package LeoulGetnetMs.ProductService.Product.Infrastructure.Primary;
import LeoulGetnetMs.ProductService.Product.Domain.Aggregiate.Product;
import LeoulGetnetMs.ProductService.ProtoGenerated.ProductInfo.ProductResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
@Slf4j
@Service
public class KafkaResource {
//    spring.kafka.producer.bootstrap-servers=localhost:9092
//    spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
//    spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.ByteArraySerializer
//
//#spring.kafka.consumer.bootstrap-servers=localhost:9092
//   #spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
//#spring.kafka.consumer.value-deserializer=org.apache.kafka.common.serialization.ByteArrayDeserializer
//#spring.kafka.consumer.group-id=kafkaconsumerid
//#spring.kafka.consumer.auto-offset-reset=earliest
//






//    @KafkaListener(topics = "testActions", groupId = "kafkaconsumerid")
//    public void consumeEvent(byte[] product) {
//        if (product != null && product.length > 0) {
//            try {
//                ProductResponse productResponse = ProductResponse.parseFrom(product);
//                System.out.println("--------------------------------------------------------------");
//                System.out.println("Received Product: " + productResponse.toString());
//                System.out.println("--------------------------------------------------------------");
//            } catch (Exception e) {
//                // LOG THE ERROR at minimum!
//                log.error(e.getMessage());
//
//                // Optionally: send to DLQ, log metrics, etc.
//            }
//        }
//    }
}