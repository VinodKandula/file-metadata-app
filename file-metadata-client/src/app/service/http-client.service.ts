import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export class FileMetadata {
  constructor() {}
}

@Injectable({
  providedIn: 'root'
})
export class HttpClientService {

  baseURL= "http://localhost:8080/filemetadata";

  constructor(private httpClient: HttpClient) { }

  // getFileMetadata() {
  //   console.log("test call");
  //   return this.httpClient.get<FileMetadata>('http://localhost:8080/filemetadata/file?path=/Users/vinodkandula/engineering/poc/FileUtilityApp/src/main/resources/data.txt');
  // }

  getFileMetadata(filePath: string) {
    console.log("test call");
    console.log(filePath);
    return this.httpClient.get<FileMetadata>(this.baseURL + '/file?path='+filePath);
  }

  getDirectoryMetadata(filePath: string) {
    return this.httpClient.get<FileMetadata>(this.baseURL + '/directory?path='+filePath);
  }

}
