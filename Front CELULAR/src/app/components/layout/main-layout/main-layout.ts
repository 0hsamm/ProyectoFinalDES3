import { Component } from '@angular/core';

import { CommonModule } from '@angular/common';

import { Conversacion } from '../../../models/conversacion';

import { SidebarComponent } from '../../sidebar/sidebar';
import { ChatListComponent } from '../../chat-list/chat-list';
import { ChatWindowComponent } from '../../chat-window/chat-window';
import { ChatInfoComponent } from '../../chat-info/chat-info';
import { FriendsPanelComponent } from '../../friends-panel/friends-panel';
import { StatesPanelComponent } from '../../states-panel/states-panel';
import { NotificationsPanelComponent } from '../../notifications-panel/notifications-panel';
import { AiPanelComponent } from '../../ai-panel/ai-panel';
import { SettingsPanelComponent } from '../../settings-panel/settings-panel';

@Component({
  selector: 'app-main-layout',

  standalone: true,

  imports: [
    CommonModule,
    SidebarComponent,
    ChatListComponent,
    ChatWindowComponent,
    ChatInfoComponent,
    FriendsPanelComponent,
    StatesPanelComponent,
    NotificationsPanelComponent,
    AiPanelComponent,
    SettingsPanelComponent
  ],

  templateUrl: './main-layout.html',

  styleUrls: ['./main-layout.css']
})
export class MainLayoutComponent {

  conversacionSeleccionada: Conversacion | null = null;

  panelActivo: string = 'chats';

  vistaChatMobile: 'lista' | 'chat' | 'detalle' =
    'lista';

  seleccionarConversacion(
    conversacion: Conversacion | null
  ): void {

    this.conversacionSeleccionada = conversacion;

    if (conversacion != null) {
      this.vistaChatMobile = 'chat';
    }
  }

  cambiarPanel(
    panel: string
  ): void {

    this.panelActivo = panel;

    if (panel != 'chats') {

      this.conversacionSeleccionada = null;
    }

    this.vistaChatMobile = 'lista';
  }

  volverALista(): void {

    this.vistaChatMobile = 'lista';
  }

  mostrarDetalle(): void {

    if (this.conversacionSeleccionada != null) {
      this.vistaChatMobile = 'detalle';
    }
  }
}
