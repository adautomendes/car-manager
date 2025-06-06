# Car Manager -  Issues found

## Issue 1

### Fault description

Operation to list all cars is not working.

### Expected behavior

With the correct request the list of cars must be returned with HTTP 200 OK.

### Steps to reproduce

- Send a request `GET http://<host>:<port>/car` to the application
- Check if response is equals to HTTP 200 OK

## Issue 2

...