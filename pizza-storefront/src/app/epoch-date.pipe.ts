import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'epochDate',
})
export class EpochDatePipe implements PipeTransform {
  transform(value: number): Date {
    return new Date(value);
  }
}
