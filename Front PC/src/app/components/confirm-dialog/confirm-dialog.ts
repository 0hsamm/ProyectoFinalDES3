import {
  Component,
  EventEmitter,
  Input,
  Output
} from '@angular/core';

import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-confirm-dialog',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './confirm-dialog.html',
  styleUrl: './confirm-dialog.css'
})
export class ConfirmDialogComponent {

  @Input() visible = false;

  @Input() title = 'Confirmar';

  @Input() message = '';

  @Input() confirmLabel = 'Aceptar';

  @Input() cancelLabel = 'Cancelar';

  @Output() readonly confirmed =
    new EventEmitter<void>();

  @Output() readonly cancelled =
    new EventEmitter<void>();
}
