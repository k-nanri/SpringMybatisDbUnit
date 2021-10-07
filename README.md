# README

PostgreSQLを使用する場合、dockerでコンテナ起動したほうが楽。

```shell
$ docker run --rm -p 5432:5432 --name postgres -d -e POSTGRES_PASSWORD=admin123 postgres
$ docker exec -it postgres bash
# createdb test -U postgres
```