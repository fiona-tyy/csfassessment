package ibf2022.batch3.assessment.csf.orderbackend.services;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import ibf2022.batch3.assessment.csf.orderbackend.models.PizzaOrder;
import ibf2022.batch3.assessment.csf.orderbackend.respositories.OrdersRepository;
import ibf2022.batch3.assessment.csf.orderbackend.respositories.PendingOrdersRepository;

@Service
public class OrderingService {

	@Autowired
	private OrdersRepository ordersRepo;

	@Autowired
	private PendingOrdersRepository pendingOrdersRepo;

	private String pricingUrl="https://pizza-pricing-production.up.railway.app";
	
	// TODO: Task 5
	// WARNING: DO NOT CHANGE THE METHOD'S SIGNATURE
	public PizzaOrder placeOrder(PizzaOrder order) throws OrderException {

		
		MultiValueMap<String,String> form = new LinkedMultiValueMap<>();
		form.add("name", order.getName());
		form.add("email", order.getEmail());
		form.add("sauce", order.getSauce());
		form.add("size", order.getSize().toString());
		form.add("thickCrust", order.getThickCrust().toString());
		String toppings = String.join(",", order.getToppings());
		form.add("toppings", toppings);
		form.add("comments", order.getComments());

		RequestEntity<MultiValueMap<String,String>> req = RequestEntity.post(pricingUrl+"/order")
																.contentType(MediaType.APPLICATION_FORM_URLENCODED)
																.accept(MediaType.TEXT_PLAIN)
																.body(form);
		RestTemplate template = new RestTemplate();

		ResponseEntity<String> resp = template.exchange(req, String.class);

		String[] resultArr = resp.getBody().split(",");
		String orderId = resultArr[0];
		Long epochDate = Long.parseLong(resultArr[1]);
		Date orderDate = new Date(epochDate);
		Float total = Float.parseFloat(resultArr[2]);

		order.setOrderId(orderId);
		order.setDate(orderDate);
		order.setTotal(total);

		//save to mongo
		ordersRepo.add(order);

		//save to redis
		pendingOrdersRepo.add(order);

		return order;
	}

	// For Task 6
	// WARNING: Do not change the method's signature or its implemenation
	public List<PizzaOrder> getPendingOrdersByEmail(String email) {
		return ordersRepo.getPendingOrdersByEmail(email);
	}

	// For Task 7
	// WARNING: Do not change the method's signature or its implemenation
	public boolean markOrderDelivered(String orderId) {
		return ordersRepo.markOrderDelivered(orderId) && pendingOrdersRepo.delete(orderId);
	}


}
