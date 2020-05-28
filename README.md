# File Extension Editor
## made by Donovan Adrian in Java
###### I’m sure something like this has been done before, but hey, I got bored and I decided to make a program to make my life easier. This is my first GitHub post as well, so correct me if there are any things that I should add or change to this README!


## Welcome!

As it is explained in a comment at the beginning of the FileExtensionEditor.java, this program is a formal attempt to create a program that is highly usable while also utilizing global variables. The main goal was to maximize readability and fixability by separating functions into meaningful chunks. I personally chose to utilize global variables in order to make object passing between functions less complicated.


## How Well Does This Work?

Overall, this program works fairly efficiently, with a maximum Big O of n^21 with errors. The biggest time crunches here rely on the size and amount of files that need file extension editing. I plan to place a graph below this section including some data that tracks the performance of this program in certain use cases.


## What Are Some Possible Use Cases For This Program?

This program should primarily be used for images only, although almost any file can be changed. I initially made this program because of a simple problem that I encountered quite regularly, and I’ll be transparent here. I would download many of my memes from Twitter, which would tend to download them as JPG_LARGE files, which tended to be a weird file extension if I tried to use it elsewhere. For example, there were certain cases when I tried to open or download the file on my phone and it either didn’t open properly or wouldn’t download. As a result, I eventually got fed up with this, so I decided to brute force the problem. Sure, I probably could have created a file converter of some sort that could have bulk-converted these files, but that wasn’t really my priority at the time. I would be interested in potentially pursuing a file conversion program at some point though…


## Do You Have Any Other Plans For This Program?

Certainly, the one remaining function that I wanted to include in this program was a GUI that would allow you to select (with checkboxes) which file extensions you would like to edit. At this time, I simply used a text input to be able to accomplish this, which of course could be plagued with faulty user-input. However, I am currently pursuing another project, Gifty (which I also plan to post here), which is a gift registry program that I am developing for my family. As a result of this side project, I don’t currently plan to update this program soon.


## ***Task List***

- [x] Post On GitHub
- [ ] Test Error Checking (failedRenameFileCreator)
- [ ] Work With Replacement Of Files That May Already Exist
- [ ] Change promptUserExtension() To A Checkbox Window
- [ ] Test Parallelism
