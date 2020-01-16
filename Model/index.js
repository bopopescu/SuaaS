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
messaging.requestPermission().then(function () {
    console.log('Notification permission granted.');
    messaging.getToken().then(function (token) {
        console.log(token)
    })
}).catch(function (err) {
    console.log('Unable to get permission to notify.', err);
});
messaging.onMessage(function (payload) {
    console.log('Message received. ');
    console.log(payload)
});