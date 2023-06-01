import { Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { PizzaService } from '../pizza.service';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { PizzaData } from '../model';
import { firstValueFrom } from 'rxjs';

const SIZES: string[] = [
  'Personal - 6 inches',
  'Regular - 9 inches',
  'Large - 12 inches',
  'Extra Large - 15 inches',
];

const PIZZA_TOPPINGS: string[] = [
  'chicken',
  'seafood',
  'beef',
  'vegetables',
  'cheese',
  'arugula',
  'pineapple',
];

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.css'],
})
export class MainComponent implements OnInit {
  router = inject(Router);
  pizzaSvc = inject(PizzaService);
  fb = inject(FormBuilder);

  pizzaSize = SIZES[0];
  form!: FormGroup;
  toppingsForm!: FormGroup;

  toppingsList: string[] = [];
  order!: PizzaData;

  constructor() {}

  ngOnInit(): void {
    this.form = this.createForm();
    this.toppingsForm = this.createToppingsForm();
  }

  process() {
    console.info('>> form value: ', this.form.value);
    console.info('>> toppings: ', this.toppingsForm.value);
    console.info('>> toppings: ', this.toppingsForm.value.chicken);

    if (this.toppingsForm.value.chicken) {
      this.toppingsList.push('chicken');
    }
    if (this.toppingsForm.value.seafood) {
      this.toppingsList.push('seafood');
    }
    if (this.toppingsForm.value.beef) {
      this.toppingsList.push('beef');
    }
    if (this.toppingsForm.value.vegetables) {
      this.toppingsList.push('vegetables');
    }
    if (this.toppingsForm.value.cheese) {
      this.toppingsList.push('cheese');
    }
    if (this.toppingsForm.value.arugula) {
      this.toppingsList.push('arugula');
    }
    if (this.toppingsForm.value.pineapple) {
      this.toppingsList.push('pineapple');
    }

    this.order = {
      name: this.form.value.name,
      email: this.form.value.email,
      size: this.form.value.size,
      base: this.form.value.base,
      sauce: this.form.value.sauce,
      comments: this.form.value.comments,
      toppings: this.toppingsList,
    };
    console.info('>>orders: ', this.order);

    firstValueFrom(this.pizzaSvc.placeOrder(this.order))
      .then((result) => {
        this.router.navigate(['/orders', this.order.email]);
      })
      .catch((err) => alert(JSON.stringify(err.error)));
  }

  updateSize(size: string) {
    this.pizzaSize = SIZES[parseInt(size)];
  }

  //TO DO
  invalidForm() {
    return this.form.invalid || this.toppingsList.length <= 0;
  }

  private createToppingsForm(): FormGroup {
    return this.fb.group({
      chicken: this.fb.control<boolean>(false),
      seafood: this.fb.control<boolean>(false),
      beef: this.fb.control<boolean>(false),
      vegetables: this.fb.control<boolean>(false),
      cheese: this.fb.control<boolean>(false),
      arugula: this.fb.control<boolean>(false),
      pineapple: this.fb.control<boolean>(false),
    });
  }

  private createForm(): FormGroup {
    // this.toppingsArr = this.createToppingsArr(PIZZA_TOPPINGS);

    return this.fb.group({
      name: this.fb.control<string>('', [Validators.required]),
      email: this.fb.control<string>('', [
        Validators.required,
        Validators.email,
      ]),
      size: this.fb.control<number>(0, [
        Validators.required,
        Validators.min(0),
        Validators.max(3),
      ]),
      base: this.fb.control<string>('', [Validators.required]),
      sauce: this.fb.control<string>('classic', [Validators.required]),
      // toppings:
      comments: this.fb.control<string>(''),
    });
  }
}
