import { Component, OnInit } from '@angular/core';
import { HttpClientService, FileMetadata } from '../service/http-client.service';

@Component({
  selector: 'app-filemetadata',
  templateUrl: './filemetadata.component.html',
  styleUrls: ['./filemetadata.component.css']
})
export class FilemetadataComponent implements OnInit {

  filePath = '';
  dirPath = '';
  loading = false;
  errorMessage = '';

  filemetadata:FileMetadata[];

  constructor(private httpClientService: HttpClientService) { }

  ngOnInit() {
    // this.httpClientService.getFileMetadata().subscribe(
    //   response =>this.handleSuccessfulResponse(response)
    //  );
  }

  handleSuccessfulResponse(response){
    console.log(response);
    this.filemetadata=response;
    console.log(this.filemetadata);
  }

  public getFileMetadata() {
    console.log(this.filePath);
    this.loading = true;
    this.errorMessage = '';

    this.httpClientService.getFileMetadata(this.filePath)
        .subscribe(response =>this.handleSuccessfulResponse(response),
        (error) => {
          this.errorMessage = error.error; this.loading = false; 
        },
        () => {this.loading = false;})
  }

  public getDirectoryMetadata() {
    console.log(this.dirPath);
    this.loading = true;
    this.errorMessage = '';
    
    this.httpClientService.getDirectoryMetadata(this.dirPath)
        .subscribe(response =>this.handleSuccessfulResponse(response),
        (error) => {
          this.errorMessage = error.error; this.loading = false; 
        },
        () => {this.loading = false;})
  }

}
