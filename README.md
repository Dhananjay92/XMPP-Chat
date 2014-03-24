#Conversations
Conversations is an open source XMPP client for Android 4.0+ smart phones

![alt tag](https://raw.githubusercontent.com/siacs/Conversations/master/screenshots.png)

##Design principles
* Be as beautiful and easy to use as possible without sacrificing security or
  privacy
* Rely on existing, well established protocols
* Do not require a Google Account or specifically Google Cloud Messaging (GCM)
* Require as little permissons as possible

##Features
* End-to-end encryption with either OTR or openPGP
* Holo UI
* Multiple Accounts
* Group Chats
* Contact list integration

###XMPP Features
Conversations works with every XMPP server out there. However XMPP is an extensible
protocol. These extensions are standardized as well in so called XEP’s.
Conversations supports a couple of those to make the overall userexperience better. There is a
chance that your current XMPP server does not support these extensions.
Therefore to get the most out of Conversations you should consider either switching to an
XMPP server that does or - even better - run your own XMPP server for you and
your friends.
These XEPs are - as of now:
* XEP-0198: Stream Management allows XMPP to surive small network outages and changes of the underlying TCP connection.
* XEP-0280: Message Carbons which automatically syncs the messages you send to
  your desktop client and thus allows you to switch seamlessly from your mobile
  client to your desktop client and back within one conversation.
* XEP-0237: Roster Versioning mainly to save bandwith on poor mobile connections

##FAQ
###General
####How do I install Conversations?
Conversations is entirely open source and licensed under GPLv3. So if you are a
software developer you can check out the sources from github and use ant to
build your apk file.

The more convenient way which not only gives you automatic updates but also
supports the further development of Conversations is to buy the App in the Google
Play Store.
####How do I create an account?
XMPP like email for example is a federated protocol which means that there is
not one company you can create your 'official xmpp account' with but there are
hundreds or even thousands of provider out there. To find one use a web search
engine of your choice. Or maybe your univeristy has one. Or you can run your own.
Or ask a friend to run one. Once you found one you can use Conversations to
create an account. Just select 'register new account on server' within the
create account dialog.

###Security
####Why are there to end-to-end encryption methods and which one should I choose?
In most cases OTR should be the encryption method of choice. It works out of the box with most contacts as long as they are online.
However PGP can be in some cases (carbonated messages to multiple clients) be
more flexible.
####How do I use openPGP
Before you continue reading you should notice that the openPGP support in
Conversations is marked as experimental. This is not because it will make the app
unstable but because the fundamental concepts of PGP aren't ready for a
widespread use. The way PGP works is that you trust Key IDs instead of XMPP- or email addresses. So in theory your contact list should consist of Public-Key-IDs instead of email addresses. But of course no email or xmpp client out there implements these concepts. Plus PGP in the context of instant messaging has a couple of downsides. It is vulnerable to replay attacs, it is rather verbose, decryping and encrypting takes longer than OTR. It is however asynchronous and works well with carbonated messages.

To use openpgp you have to install the opensource app OpenKeychain (www.openkeychain.org) and then long press on the account in manage accounts and choose renew PGP announcement from the contextual menu.
