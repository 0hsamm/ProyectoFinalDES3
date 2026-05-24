import { Component } from '@angular/core';

import { CommonModule } from '@angular/common';

import {
  ToastMessage,
  ToastService
} from '../../services/toast.service';

@Component({
  selector: 'app-toast-container',
  standalone: true,
  imports: [
    CommonModule
  ],
  templateUrl: './toast-container.html',
  styleUrl: './toast-container.css'
})
export class ToastContainerComponent {

  messages$;

  constructor(
    private toastService: ToastService
  ) {

    this.messages$ =
      this.toastService.messages$;
  }

  close(
    toast: ToastMessage
  ): void {

    this.toastService.remove(toast.id);
  }
}
