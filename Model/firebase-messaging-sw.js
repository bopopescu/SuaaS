importScripts('https://www.gstatic.com/firebasejs/4.10.0/firebase-app.js');
importScripts('https://www.gstatic.com/firebasejs/4.10.0/firebase-messaging.js');
var config = {
    apiKey: "AIzaSyCehG49IQRrpo6smULR-Clw3VjZHDX85PU",
    authDomain: "healthy-topic-200211.firebaseapp.com",
    databaseURL: "https://healthy-topic-200211.firebaseio.com",
    projectId: "healthy-topic-200211",
    storageBucket: "healthy-topic-200211.appspot.com",
    messagingSenderId: "136439802744"
};
firebase.initializeApp(config);
const messaging = firebase.messaging();