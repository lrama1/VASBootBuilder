export class ListWrapper<T>{
  constructor(public rows: Array<T>, public totalRecords: number, public lastPage: number){}
}
