import { Component } from '@angular/core';

import { SidebarComponent } from '../../sidebar/sidebar';
import { ChatListComponent } from '../../chat-list/chat-list';
import { ChatWindowComponent } from '../../chat-window/chat-window';
import { ChatInfoComponent } from '../../chat-info/chat-info';

@Component({
  selector: 'app-main-layout',

  standalone: true,

  imports: [
    SidebarComponent,
    ChatListComponent,
    ChatWindowComponent,
    ChatInfoComponent
  ],

  templateUrl: './main-layout.html',

  styleUrls: ['./main-layout.css']
})
export class MainLayoutComponent {

}
