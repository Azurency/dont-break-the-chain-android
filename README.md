# Don’t break the chain (android)
Don’t break the chain is an application based on the method [“don’t break the chain”](http://lifehacker.com/281626/jerry-seinfelds-productivity-secret "don't break the chain") _by Jerry Seinfeld_.

### How it works
1. pick a task
2. mark each day you do your task on a calendar
3. don’t break the chain
You can use that simple method for every kind of tasks, it can be **building a website**, **writing**, **exercise**, **learn Chinese**…

> Skipping one day makes it easier to skip the next.

For instance, let’s say you have the goal **write**

> It doesn't particularly matter what you write. Blogs, articles, scripts, your memoir. It can be anything, as long as you're actively and routinely pushing yourself.

### About this project
The first version of this project is developed as part of an assignment for the _IUT of Orléans, IT department_ in which we need to develop, in group of two, an android application. The subject was free but the application need to contain a few elements :
* a database
* a ListView
* some preferences
* at least on call to an intent
* some fragments to adapt the layout to the device orientation

### The database
The database will store the different tasks and their corresponding chains (a task can be composed of multiple chains).

Task | Chain
---- | -----
**idT** | **idC**
title | **idT**
notificationHour | firstDate
ringToneURI | lastDate

### First mockup
This is the first mockup of the application _(11/03/2015)_

![don't break the chain - mockup 1 for android](http://f.cl.ly/items/1K1O45450v0e1J133J45/mockup-android.jpg)
