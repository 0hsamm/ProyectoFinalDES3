import { Injectable }
  from '@angular/core';

import { HttpClient }
  from '@angular/common/http';

import { Observable }
  from 'rxjs';

import { environment }
  from '../../environment/environment';

@Injectable({

  providedIn: 'root'

})

export class ConversacionService {

  private apiUrl =
    environment.apiUrl +
    '/conversaciones';

  constructor(
    private http: HttpClient
  ) {}

  obtenerConversaciones():
    Observable<any[]> {

    return this.http.get<any[]>(

      this.apiUrl

    );
  }

}
