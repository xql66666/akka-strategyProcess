package com.xuqianlei.boot.akka.config;

import akka.actor.typed.ActorSystem;
import com.xuqianlei.boot.akka.actor.root.RootBehaviorActor;
import com.xuqianlei.boot.akka.entity.CommandContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AkkaConfiguration {

    //@Autowired
    //private ApplicationContext applicationContext;
    //
    //@Bean
    //public ActorSystem actorSystem() {
    //    ActorSystem actorSystem = ActorSystem.create("realtime_actorSystem");
    //    SPRING_EXTENSION_PROVIDER.get(actorSystem).initialize(applicationContext);
    //    return actorSystem;
    //}

    @Autowired
    private ActorServiceFactory actorServiceFactory;

    @Bean
    public ActorSystem<CommandContext> actorSystem() {
        return ActorSystem.create(RootBehaviorActor.create(actorServiceFactory), "realtime_actorSystem");
    }


}
