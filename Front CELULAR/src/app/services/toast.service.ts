import { Injectable } from '@angular/core';

import { BehaviorSubject } from 'rxjs';

export type ToastType =
  'success' |
  'error' |
  'info' |
  'warning';

export interface ToastMessage {

  id: number;

  type: ToastType;

  title: string;

  message?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ToastService {

  private messagesSubject =
    new BehaviorSubject<ToastMessage[]>([]);

  messages$ =
    this.messagesSubject.asObservable();

  private nextId = 1;

  success(
    title: string,
    message?: string
  ): void {

    this.show(
      'success',
      title,
      message
    );
  }

  error(
    title: string,
    message?: string
  ): void {

    this.show(
      'error',
      title,
      message
    );
  }

  info(
    title: string,
    message?: string
  ): void {

    this.show(
      'info',
      title,
      message
    );
  }

  warning(
    title: string,
    message?: string
  ): void {

    this.show(
      'warning',
      title,
      message
    );
  }

  remove(
    id: number
  ): void {

    this.messagesSubject.next(
      this.messagesSubject.value.filter(
        (message) => message.id != id
      )
    );
  }

  private show(
    type: ToastType,
    title: string,
    message?: string
  ): void {

    const toast: ToastMessage = {
      id: this.nextId++,
      type,
      title,
      message
    };

    this.messagesSubject.next([
      toast,
      ...this.messagesSubject.value
    ]);

    setTimeout(
      () => this.remove(toast.id),
      4200
    );
  }
}
