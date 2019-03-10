# json-array-writer

Spring Boot App for saving various endpoints from `https://jsonplaceholder.typicode.com`

App saves all array's elements to separate json files, if endpoint is returning object, then it is threaded like one-element-array.

App saves files to `lt.ro.fachmann.jsonarraywriter.export-directory` directory. Files are named with `requestName` and by `"id"` property from their body. If absent or file name is illegal as `id`, random UUID is used.

App supports streams (`application/stream+json`) so progress of saving files can be observed via e.g. `curl`. **Files are saved in parallel!**

## Usage

`curl http://localhost:8080/executeWriting/{requestName}?endPoint=https%3A%2F%2Fjsonplaceholder.typicode.com%2Ftodos`

* `requestName` - internal name of endpoint (for naming folder with jsons)
* `endPoint` - endPoint navigating to json array (default: `lt.ro.fachmann.jsonarraywriter.default-endpoint`)

## Default properties

```properties
lt.ro.fachmann.jsonarraywriter.export-directory=.
lt.ro.fachmann.jsonarraywriter.default-endpoint=https://jsonplaceholder.typicode.com/posts
```
