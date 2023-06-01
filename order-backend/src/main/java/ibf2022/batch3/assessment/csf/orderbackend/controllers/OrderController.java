package ibf2022.batch3.assessment.csf.orderbackend.controllers;

import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import ibf2022.batch3.assessment.csf.orderbackend.models.PizzaOrder;
import ibf2022.batch3.assessment.csf.orderbackend.services.OrderException;
import ibf2022.batch3.assessment.csf.orderbackend.services.OrderingService;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;

@RestController
@RequestMapping
public class OrderController {

	@Autowired
	private OrderingService orderSvc;


	// TODO: Task 4 - POST /api/order
	@PostMapping(path = "/api/order")
	public ResponseEntity<String> sendOrder(@RequestBody String payload){

		// System.out.println(">>payload: " + payload);

		JsonReader reader = Json.createReader(new StringReader(payload));
		JsonObject obj = reader.readObject();

		PizzaOrder order = new PizzaOrder();
		order.setName(obj.getString("name"));
		order.setEmail(obj.getString("email"));
		order.setComments(obj.getString("comments"));
		order.setSize(obj.getInt("size"));
		order.setThickCrust(obj.getString("base")== "thick"? true: false);
		order.setSauce(obj.getString("sauce"));
		JsonArray toppings = obj.getJsonArray("toppings");
		List<String> toppingList = new LinkedList<>();
		for(JsonValue t : toppings){
			toppingList.add(t.toString());

		}
		order.setToppings(toppingList);

		try {
			PizzaOrder orderResult = orderSvc.placeOrder(order);

			JsonObject resp = Json.createObjectBuilder()
									.add("orderId", orderResult.getOrderId())
									.add("date", orderResult.getDate().toInstant().toEpochMilli())
									.add("total", orderResult.getTotal())
									.add("name", orderResult.getName())
									.add("email", orderResult.getEmail())
									.build();

			return ResponseEntity.status(HttpStatus.ACCEPTED)
									.body(resp.toString());

			
		} catch (OrderException e) {
			e.printStackTrace();
			JsonObject resp = Json.createObjectBuilder()
									.add("error", e.getMessage())
									.build();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
								.body(resp.toString());
		}
		

	}


	// TODO: Task 6 - GET /api/orders/<email>
	@GetMapping(path = "/api/orders/{email}")
	public ResponseEntity<String> getOrdersByEmail(@PathVariable String email){

		List<PizzaOrder> orders = orderSvc.getPendingOrdersByEmail(email);

		JsonArrayBuilder arrBuilder = Json.createArrayBuilder();

		for(PizzaOrder o: orders){
			JsonObject obj = Json.createObjectBuilder()
									.add("orderId", o.getOrderId())
									.add("date", o.getDate().toInstant().toEpochMilli())
									.add("total", o.getTotal())
									.build();
			arrBuilder.add(obj);
		}
		
		return ResponseEntity.status(HttpStatus.OK)
							.body(arrBuilder.build().toString());
	}


	// TODO: Task 7 - DELETE /api/order/<orderId>

	@DeleteMapping(path = "/api/order/{orderId}")
	public ResponseEntity<String> orderDelivered(@PathVariable String orderId){

		boolean result = orderSvc.markOrderDelivered(orderId);

		if (!result){
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
								.body(Json.createObjectBuilder().add("error", "orderId not found").build().toString());
		}

		return ResponseEntity.ok("{}");
	}

}
