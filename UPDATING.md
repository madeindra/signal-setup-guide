Signal just recently release ther v3.x server code. To pull update from their repo you can follow this guide.
Warning: the previous version is v2.92, updating to v.3.x might break some functionalities.

1. Open https://github.com/signalapp/Signal-Server

2. Fork the repository to your own git Repo

3. Copy the ssh (e.g. git@github.com:yourusername/Signal-Server.git)

4. Open signal-server code in your editor (e.g. VSCode)

5. Run the command in terminal (with your own ssh)

```
git remote add new_origin git@github.com:yourusername/Signal-Server.git
git pull new_origin  
git rebase new_origin/master
```

6. There will be some conflict if you made any modification to the server, you need to solve it manually, there is no other way.
