import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { FilemetadataComponent } from './filemetadata/filemetadata.component';


const routes: Routes = [
  { path:'', component: FilemetadataComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
