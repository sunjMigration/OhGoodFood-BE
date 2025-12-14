//package kr.co.ohgoodfood._legacy;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import com.google.gson.Gson;
//
//import kr.co.ohgoodfood.payment.service.PayService;
//import lombok.RequiredArgsConstructor;
//import okhttp3.Credentials;
//import okhttp3.MediaType;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;
//
//@Controller
//@RequiredArgsConstructor
//public class PayController {
//    @Value("${toss.clientKey}")
//    private String CLIENT_KEY;
//    @Value("${toss.secretKey}")
//    private String SECRET_KEY;
//
//    private final PayService payService;
//    private final OkHttpClient okHttpClient;
//
//    // @PostMapping("/payment/ready")
//    // @ResponseBody
//    // public Map<String, Object> readyPayment(@RequestParam int amount) {
//    //     Map<String, Object> map = new HashMap<>();
//    //     map.put("clientKey", CLIENT_KEY);
//    //     map.put("amount", amount); // 테스트용 10,000원
//    //     map.put("orderId", "order_" + UUID.randomUUID());
//    //     return map;
//    // }
//
//    @PostMapping("/payment/insert")
//    @ResponseBody
//    public Map<String, Object> insertPayment(@RequestParam String user_id, @RequestParam String store_id,
//            @RequestParam int product_no, @RequestParam int quantity, @RequestParam int paid_price, @RequestParam int paid_point) {
//        String orderId = "order_" + UUID.randomUUID();
//        if(payService.checkProductAmount(product_no, quantity) && payService.getStoreStatus(store_id)) {
//            payService.insertOrderAndPaid(user_id, store_id, product_no, quantity, paid_price,orderId, paid_point);
//            Map<String, Object> map = new HashMap<>();
//            map.put("result", "success");
//            map.put("orderId", orderId);
//            map.put("amount", paid_price);
//            map.put("clientKey", CLIENT_KEY);
//            return map;
//        } else {
//            Map<String, Object> map = new HashMap<>();
//            map.put("result", "fali");
//            map.put("orderId", orderId);
//            map.put("amount", paid_price);
//            return map;
//        }
//    }
//
//    @GetMapping("/payment/success")
//    public String confirmPayment(@RequestParam String paymentKey,
//                                 @RequestParam String orderId,
//                                 @RequestParam int amount,
//                                 Model model) throws IOException {
//
//        Map<String, Object> jsonMap = Map.of(
//            "paymentKey", paymentKey,
//            "orderId", orderId,
//            "amount", amount
//        );
//
//        String json = new Gson().toJson(jsonMap);
//
//        Request request = new Request.Builder()
//            .url("https://api.tosspayments.com/v1/payments/confirm")
//            .post(RequestBody.create(json, MediaType.get("application/json")))
//            .addHeader("Authorization", Credentials.basic(SECRET_KEY, ""))
//            .addHeader("Content-Type", "application/json")
//            .build();
//
//        try (Response response = okHttpClient.newCall(request).execute()) {
//            if (response.isSuccessful() && payService.checkProductAmountByPaidCode(orderId)
//            && payService.getStoreStatusByPaidCode(orderId)) {
//                payService.updateOrderStatusAndPaidStatus(orderId);
//                payService.updateUserPointByPaidCode(orderId);
//                return "redirect:/user/orderList";
//            } else {
//                payService.updateOrderCanceldFromByPaidCode(orderId);
//                model.addAttribute("orderId", orderId);
//                // toss orderId <<<< 환불 요청 해야함
//                return "redirect:/user/paidfail";
//            }
//        }
//    }
//
//    @GetMapping("/payment/fail")
//    public String failPage(@RequestParam String paymentKey,
//                        @RequestParam String orderId,
//                        @RequestParam int amount,
//                        Model model) {
//        payService.updateOrderCanceldFromByPaidCode(orderId);
//        model.addAttribute("orderId", orderId);
//        return "redirect:/user/paidfail";
//    }
//}
