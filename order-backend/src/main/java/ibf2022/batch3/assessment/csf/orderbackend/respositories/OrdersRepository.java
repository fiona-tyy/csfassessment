package ibf2022.batch3.assessment.csf.orderbackend.respositories;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mongodb.client.result.UpdateResult;

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
							.append("total", order.getTotal().toString())
							.append("name", order.getName())
							.append("email", order.getEmail())
							.append("sauce", order.getSauce())
							.append("size", order.getSize())
							.append("crust", order.getThickCrust()? "thick": "thin")
							.append("toppings", order.getToppings());
		
		if(order.getComments().trim().length() > 0){
			doc.append("comments", order.getComments());
		}

		mongoTemplate.insert(doc, collectionName);

	}
	
	// TODO: Task 6
	// WARNING: Do not change the method's signature.
	// Write the native MongoDB query in the comment below
	//   Native MongoDB query here for getPendingOrdersByEmail()
	/*
	db.orders.find({
		$and:[
			{email: <email>},
			{delivered: {$exists: false}}
			]
		},
		{_id:1, total:1, date:1})
		.sort({date:-1})
	 */ 
	public List<PizzaOrder> getPendingOrdersByEmail(String email) {
		Criteria criteria = Criteria.where("email").is(email);
		Query query = new Query(criteria);
		query.addCriteria(Criteria.where("delivered").exists(false));
		query.with(Sort.by(Direction.DESC, "date"));
		query.fields()
			.include("_id", "total", "date");
		
		List<PizzaOrder> orders = mongoTemplate.find(query, Document.class, collectionName)
											.stream()
											.map(d -> {
												PizzaOrder p = new PizzaOrder();
												p.setOrderId(d.getString("_id"));
												p.setDate(d.getDate("date"));
												p.setTotal(Float.parseFloat(d.getString("total")));
												return	p;
											})
											.toList();

		return orders;
	}

	// TODO: Task 7
	// WARNING: Do not change the method's signature.
	// Write the native MongoDB query in the comment below
	//   Native MongoDB query here for markOrderDelivered()
	/*
	db.orders.update(
		{_id: <orderId>},
		{$set: {delivered: true}}
		)
	 */ 
	public boolean markOrderDelivered(String orderId) {

		Criteria criteria = Criteria.where("_id").is(orderId);

		Query query = new Query(criteria);

		Update update = new Update().set("delivered", true);

		UpdateResult result = mongoTemplate.updateFirst(query, update, collectionName);

		if(result.getModifiedCount() > 0){
			return true;
		}

		return false;
	}


}
