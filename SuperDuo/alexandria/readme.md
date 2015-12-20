# Extra error cases found

Handle the case when a null response is return. This can happen if the server is down for example. Now an "internal server error" is raised and a message print to the user.
To simulate it, it is possible use a corrupt URI (https://www.googleapis.coms/v1 for example).

Handle the error when most of the fields are null (test with the isbn 9782754803199 which only contains a title).
A book must a least contained a title, subtitle, author and category. Image is optional.
If one of the field is missing, display a "Corrupt data error"
This also avoid to get a NullPointerException when the "authors" is null and add more consistency to the data.