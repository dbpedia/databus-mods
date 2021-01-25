package dev;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Observable;
import java.util.Observer;

@SpringBootApplication
public class ObserverApplication {

    public static void main(String[] args) {
        try(ConfigurableApplicationContext ctx = SpringApplication.run(ObserverApplication.class, args)){
            Observable observable = ctx.getBean(Observable.class);
            observable.notifyObservers("Hello");
        }
    }

    @Bean
    public Observable myObservable(){
        return new MyObservable();
    }

    @Bean
    MyObserver observer1(){
        return new MyObserver(1);
    }

    @Bean
    MyObserver observer2(){
        return new MyObserver(2);
    }

    @Autowired
    public void configureObservers(Observable myObservable, MyObserver observer1, MyObserver observer2){
        myObservable.addObserver(observer1);
        myObservable.addObserver(observer2);
    }

    static class MyObserver implements Observer {

        int id;

        public MyObserver(int id) {
            this.id = id;
        }

        @Override
        public void update(Observable o, Object arg) {
            System.out.println("Observer: " + id + ", Received object: " + arg);
        }
    }

    static class MyObservable extends Observable {
        @Override
        public void notifyObservers(Object arg){
            this.setChanged();
            super.notifyObservers(arg);
        }
    }
}