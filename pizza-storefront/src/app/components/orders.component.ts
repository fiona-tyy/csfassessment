import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PizzaService } from '../pizza.service';
import { Observable, firstValueFrom } from 'rxjs';
import { PizzaOrderSummary } from '../model';

@Component({
  selector: 'app-orders',
  templateUrl: './orders.component.html',
  styleUrls: ['./orders.component.css'],
})
export class OrdersComponent implements OnInit {
  activatedRoute = inject(ActivatedRoute);
  pizzaSvc = inject(PizzaService);
  orderList$!: Observable<PizzaOrderSummary[]>;

  email = '';

  ngOnInit(): void {
    this.email = this.activatedRoute.snapshot.params['email'];
    this.orderList$ = this.pizzaSvc.getOrders(this.email);
  }

  delivered(orderId: string) {
    console.info('print order id: ', orderId);
    firstValueFrom(this.pizzaSvc.delivered(orderId))
      .then((result) => (this.orderList$ = this.pizzaSvc.getOrders(this.email)))
      .catch((err) => alert(JSON.stringify(err.error)));
    this.orderList$ = this.pizzaSvc.getOrders(this.email);
  }
}
