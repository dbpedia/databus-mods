//package org.dbpedia.databus.mods.worker.springboot.test;
//
//import org.dbpedia.databus.mods.worker.springboot.controller.BasicWorkerApi;
//import org.dbpedia.databus.mods.worker.springboot.controller.WorkerApi;
//import org.dbpedia.databus.mods.worker.springboot.service.ActivityService;
//import org.junit.jupiter.api.Test;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.boot.web.server.LocalServerPort;
//import org.springframework.http.ResponseEntity;
//
//@SpringBootTest(
//        webEnvironment = WebEnvironment.RANDOM_PORT,
//        classes = BasicWorkerApp.class
//)
//public class BasicWorkerTest {
//
//    @LocalServerPort
//    private int port;
//
//    @Autowired
//    private TestRestTemplate restTemplate;
//
//    @Autowired
//    private ActivityService activityService;
//
//    @Autowired
//    private WorkerApi workerApi;
//
////    @Test
////    public void contextLoad() throws Exception{
////        assert(activityService != null);
////        assert(workerApi != null);
////        assert(workerApi instanceof BasicWorkerApi);
////    }
////
////    @Test
////    public void test() {
////
////        String path = "/publisher/group/artifact/version/file/activity";
////
////        ResponseEntity<String> rp = this.restTemplate.getForEntity("http://localhost:"+port+"/"+path,String.class);
////        System.out.println(rp.getStatusCode());
////
////        //        System.out.println(rp.getBody());
////    }
//}
