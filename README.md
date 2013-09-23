## What is this?

This application allows you to create and pick between multiple notebooks when using Wacom’s Bamboo Paper.
Note that Bamboo Paper seems to be an exclusive for Samsung and as such behaves really poorly on other phones.

This application is also the very first application using not just RootTools, but more specifically RootTools’ new Java interfaces that allow developers to write root applications entirely in Java.

Ironically I had to add a bit of JNI code for those things that Java6 does not support (but Java7 does) e.g. handling symbolic links. However, most applications using RootTools could be coded entirely in pure Java.

## How to use this application

You need Bamboo Paper already installed (!) and for your phone to be *rooted* -- I cannot emphasize enough how important it is for your device to be rooted and how this app will *not* work if your device isn’t.

Simply launch the app, create a new notebook, click the notebook to switch to using it in Bamboo Paper. That’s it.

To delete a notebook (careful!) swipe its list entry and tap ‘Delete.’ This will not delete the content of the notebook but it sure will kill its table of content. If you re-create a notebook with the same name, it will be a _different_ notebook.

Warning: this is the first release of this application, which I wrote in my spare time, so it’s very basic and you may end up shooting yourself in the foot if, for instance, you delete the currently active notebook. I plan on fixing most issues as soon as possible.

## How were RootTools’ new Java abilities leveraged?

First, you will notice that in this particular branch, I am not using RootTools’ jar file; instead, the library is included as regular source code; this is due to being based on the Developmental branch.

90% of the magic is found in `RootTools/containers/RootClass.java`

All you need to know if how to use RootClass to generate a proper application.

## So, how do I use RootClass?

1. You need to tag classes that will be available to run as root. In your source files, simply add this annotation: `@RootClass.Candidate` (*)

2. Build your project as you usually would, using your usual target.

3. Now, it is time to wrap your root Java code in its own wrapper. To do so, change your build target to ‘RootClass’ and run it. It will look for annotations and include your classes accordingly. If everything goes according to plan, the root container will be generated. Its name is `anbuild.dex`

4. I hope to get rid of this step as we make progress beta testing various IDEs: copy `anbuild.dex` from where it was generated to `res/raw/`

5. Build your project again! The container will be picked up and included in your application. Note that an incremental build ought to be enough, making the process very fast. 

6. Install the application on your test device/emulator. It will automatically extract its container to its `<data>/files/` directory.

7. Run your application.

(*) For instance, in this project, we will delegate most of our root work to a class called `RootHelper`

    @RootClass.Candidate
    public class RootHelper {

Because I also wanted to make my logging wrapper available to RootHelper, I also annotated `CLog.java` so that it would be picked up during the build process.

## Is there a way to speed up this process?

When making modifications to your root code -- in my case this would be in `RootHelper.java` -- you may not want to repeat all the steps above.

A somewhat more expedient way to test your modifications is:

1. Execute above step #2 -- this will be quick since it’s an incremental build; or you could even decide to only compile your root classes

2. “adb push” `anbuild.dex` from where it was generated to `<data>/files/`

3. Keep testing your application: you just hot-swapped your root code!

## What about JNI?

So, Java is great and all but Java 6 having its own limitations, you may wish to add some native library code to it.

Simple: follow the same steps you would when writing non-root native code.

Only one difference: when loading your native library, use `System.load()` rather than `System.loadLibrary()` and prefix the library path with the current working directory.

For instance:

    static {
        System.load(System.getProperty("user.dir") + "/lib/libtinylib.so");
    }

*How come this is my current working directory?*

It is because you told RootTools to make that happen using the new shell command: `useCWD(Context)`

If you look at `initBamboo()`'s source code, you will see:

    try {
        Shell shell = RootTools.getShell(true);
        shell.useCWD(this);
        
meaning that from now on all commands you run using that shell will be executed in your application's data directory rather than your device's root.

## Your code is not properly licensed!

Yes it is. Oh, wait. You mean it does not include the proper acknowledgments for RootTools itself as well as 47 Degree's SwipeListView.
 
You're right. I need to get around to doing it. Note that the app doesn't even have an 'About' box at this point :/

