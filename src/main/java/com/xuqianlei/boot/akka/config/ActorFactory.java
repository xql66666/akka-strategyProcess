package com.xuqianlei.boot.akka.config;

import com.xuqianlei.boot.akka.service.ActionService;
import org.springframework.stereotype.Component;

/**
 * @Description:  actor中所以的bean调用 都由此bean为入口
 * @Author: xuqianlei
 * @Date: 2024/01/08/15:55
 */
@Component
public class ActorFactory {

    //@Autowired
    //private ActorSystem actorSystem;
    //
    //
    //public void notifyActor(String name, String message) {
    //    ActorRef actorRef = actorSystem.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(actorSystem)
    //            .props(name), name + "_" + UUID.randomUUID());
    //    actorRef.tell(message, ActorRef.noSender());
    //}

}
