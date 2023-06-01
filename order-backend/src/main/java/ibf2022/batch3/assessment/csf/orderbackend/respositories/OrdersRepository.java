package ibf2022.batch3.assessment.csf.orderbackend.respositories;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import ibf2022.batch3.assessment.csf.orderbackend.models.PizzaOrder;

@Repository
public class OrdersRepository {

	@Autowired
	private MongoTemplate mongoTemplate;

	private String collectionName = "orders";

	// TODO: Task 3
	// WARNING: Do not change the method's signature.
	// Write the native MongoDB query in the comment below
	//   Native MongoDB query here for add()
	/*
	db.orders.insert({
		_id: <orderId as String>,
		date: <date as Date>,
		total: <total as String>,
		name: <name as String>,
		email: <email as String>,
		sauce: <sauce as String>,
		size : <size as Integer>,
		crust: <"thick" or "thin",
		comments: <comments as String>,
		toppings: [<topping0> ...]
	})
	 */ 
	public void add(PizzaOrder order) {
		Document doc = new Document()
							.append("_id", order.getOrderId())
							.append("date", order.getDate())
							.append("total", order.getTotal())
							.append("name", order.getName())
							.append("email", order.getEmail())
							.append("sauce", order.getSauce())
							.append("size", order.getSize())
							.append("crust", order.getThickCrust()? "thick": "thin")
							.append("comments", order.getComments())
							.append("toppings", order.getTopplings());
		
		if(order.getComments().trim().length() > 0){
			doc.append("comments", order.getComments());
		}

		mongoTemplate.insert(doc, collectionName);

	}
	
	// TODO: Task 6
	// WARNING: Do not change the method's signature.
	// Write the native MongoDB query in the comment below
	//   Native MongoDB query here for getPendingOrdersByEmail()
	public List<PizzaOrder> getPendingOrdersByEmail(String email) {

		return null;
	}

	// TODO: Task 7
	// WARNING: Do not change the method's signature.
	// Write the native MongoDB query in the comment below
	//   Native MongoDB query here for markOrderDelivered()
	public boolean markOrderDelivered(String orderId) {

		return false;
	}


}
