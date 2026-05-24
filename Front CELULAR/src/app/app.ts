import { Component }
  from '@angular/core';
import { OnInit }
  from '@angular/core';

import { RouterOutlet }
  from '@angular/router';

import { ToastContainerComponent }
  from './components/toast-container/toast-container';

import { ThemeService }
  from './services/theme.service';

@Component({

  selector: 'app-root',

  standalone: true,

  imports: [
    RouterOutlet,
    ToastContainerComponent
  ],

  templateUrl: './app.html',

  styleUrl: './app.css'
})

export class App
  implements OnInit {

  constructor(
    private themeService: ThemeService
  ) {}

  ngOnInit(): void {

    this.themeService.inicializar();
  }

}
