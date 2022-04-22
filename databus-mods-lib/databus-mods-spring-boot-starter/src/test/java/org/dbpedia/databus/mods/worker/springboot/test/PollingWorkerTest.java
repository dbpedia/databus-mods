//package org.dbpedia.databus.mods.worker.springboot.test;
//
//import org.dbpedia.databus.mods.worker.springboot.controller.PollingBasedWorkerApi;
//import org.dbpedia.databus.mods.worker.springboot.controller.WorkerApi;
//import org.dbpedia.databus.mods.worker.springboot.service.ActivityService;
//import org.dbpedia.databus.mods.model.ModActivity;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.boot.web.server.LocalServerPort;
//import org.springframework.http.*;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//
//@SpringBootTest(
//        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
//        classes = PollingWorkerApp.class
//)
//public class PollingWorkerTest {
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
//    @Autowired
//    private ModActivity modActivity;
//
//    @Test
//    public void contextLoad() throws Exception{
//        assert(activityService != null);
//        assert(workerApi != null);
//        assert(workerApi instanceof PollingBasedWorkerApi);
//        assert(modActivity != null);
//    }
//
//    @Test
//    public void invokeAndPollActivity() {
//        String path = "/vehnem/paper-supplements/demo-graph/20210301/demo-graph.nt.gz";
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
//        map.add("source", "file:///proc/cpuinfo");
//
//        int lastStatusCode = 0;
//
//        ResponseEntity<String> rePOST = this.restTemplate.exchange(
//                "http://localhost:"+port+path+"/activity",
//                HttpMethod.POST,
//                new HttpEntity<>(map,headers),
//                String.class);
//
//        lastStatusCode = rePOST.getStatusCodeValue();
//        assert(lastStatusCode == 202);
//
//        String body = "";
//        while (lastStatusCode == 202) {
//            ResponseEntity<String> reGET = this.restTemplate.getForEntity(
//                    "http://localhost:"+port+path+"/activity",
//                    String.class);
//            lastStatusCode = reGET.getStatusCodeValue();
//            body = reGET.getBody();
//        }
//
//        System.out.println(body);
//        assert(lastStatusCode == 200);
//    }
//}
