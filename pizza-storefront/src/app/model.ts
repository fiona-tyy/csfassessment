export interface PizzaData {
  name: string;
  email: string;
  size: number;
  base: string;
  sauce: string;
  toppings: string[];
  comments: string;
}

export interface PizzaResponse {
  orderId: string;
  date: number;
  name: string;
  email: string;
  total: number;
}

export interface PizzaOrderSummary {
  orderId: string;
  date: number;
  total: number;
}
