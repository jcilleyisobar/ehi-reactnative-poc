Enterprise Mobile
=================

|    Branch     |    Build Status   |
|:-------------:|:-----------------:|
| Develop       | ![Develop Status] |
| Release 2.4.0 | ![Release Status] |

[Develop Status]:https://travis-ci.com/RoundarchLabs/ehi-ios.svg?token=obK1JXnFzBpsP6tqQWJD&branch=develop
[Release Status]:https://travis-ci.com/RoundarchLabs/ehi-ios.svg?token=obK1JXnFzBpsP6tqQWJD&branch=release/2.4.0

## Getting Started
Open `Enterprise.xcworkspace` and attempt to run the application.  

## Git Workflow 
We're using `git-flow` to manage our branching strategy. You can install it (if necessary) using brew:

```sh
$ brew install git-flow
$ git flow init
```

When running `init`, you should just accept all the defaults by hitting enter. As you complete features,
please submit pull requests.

## Building / Dependencies
We're managing dependencies using Cocoapods. The Pods are included in the repo, so you 
*should* be able to pull-and-build. In the event you aren't, or if you need to update the 
pods, then you should install [Cocoapods](http://http://cocoapods.org/) via the following 
steps:

If you only have the system Ruby, install [rbenv](https://github.com/sstephenson/rbenv)

```sh
$ brew update
$ brew install rbenv ruby-build
```

After installation, add `eval "$(rbenv init -)"` to your shell configuration, and then 
re-source your shell.

Install a Ruby using `rbenv`: 

```sh
$ rbenv install -l
$ rbenv install 2.2.0 # or some other version
```

Install the Cocoapods Rubygem:

```sh
$ gem install cocoapods
$ pod setup # you only need to do this once 
```

Update the Pods:

```sh
$ pod install
```

## Colors
We're auto-generating the color category/palette using another Rubygem, [Spectra](https://github.com/derkis/Spectra):

```sh
$ gem install spectra
$ spectra generate
```

You should run this command at least once so that it generates a palette for you. If you want
to add new colors, you should modify `spectrum.rb` and then re-issue the command. Any changes made
to the color category directly will get blown away when it's regenerated.

## Utils

Just run

```sh
$ ./bootstrap.sh
```

![alt text](/Utils/Images/bootstrap.gif "Running bootstrap")

To save time! It'll install...

### Xcode Templates

Will create a skeleton for either Storyboard files / Interface Builder with its view model class.

![alt text](/Utils/Images/xcode%20templates.png "Xcode Templates")

### Git hooks

The `commit_msg` will ensure that, as long as you are following `git-flow` naming convention, the ticket number is appended at the beginning of all the commit messages (when applicable).

```sh
$ feature/EA-1234> git commit -m "Adding a cool feature"
$ feature/EA-1234> git log -n 1"
$ commit b564832a22235ca0e612fe05949d6f470a1bb28b
$ 
$     EA-1234 :: Adding a cool feature
$ (END)
```
