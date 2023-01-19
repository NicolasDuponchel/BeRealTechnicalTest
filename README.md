# BeReal technical test

## Candidate

Nicolas Duponchel

Senior Android engineer at GoPro

nicolas.duponchel@gmail.com

0682750834


## Technical test resume

Of course I don't have time to modularise now, but I "simulated" a modularised & clean archi approach with the packages names 
(login/domain/repo, presentation, objectgraph/app) 

I built the app with a kind of MVVM pattern : 
- UI handles user's interaction and notifies a listener (IMainListener).
- MainViewModel implem this listener and treats the stuff to do (mainly calling the REST API), then updates its MutableModel
- An immutable model as State is exposed to the UI
- The ui recompose its views based on that changes
- And we've got the loop closed.


<p align="center">
    <img src="demo.gif" width="200">
</p>

#### Used techno
- Hilt for injection
- Compose for UI
- ViewModels & LiveData for presentation layer (the UI only responds to an updated model hold by VM and "observed" via LiveData converted to State)
- Kotlin (obviously ... but I just wanted to mention it because I love this language)
- Coroutine for async call
- Retrofit for API calls
- Kotlinx Serialization (not hardly used but still mentioning it)


## Goal & instructions

REST API to navigate a hierarchy of folders.
Server is available at http://163.172.147.216:8080

The goal is to:
- [X] connect to the server
- [X] navigate file hierarchy
- [X] display a fullscreen image

Plus, as a bonus if you have extra time:
- [X] create a folder
- [X] delete a folder
- [X] login page
- [ ] upload a local file from the phone

You’re not expected to do everything perfectly on the 'goal' list, this would be too long. This is your opportunity to showcase your skills and you can spend more time on one area vs another (UI, architecture…)
It’s expected you'll spend approx. 3 - 4 hours on the test, but you can spend a little bit more than that if you want (but we appreciate you are busy).

