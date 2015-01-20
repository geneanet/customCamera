# org.geneanet.customcamera

Ce plugin défini une variable globale navigator.GeneanetCustomCamera contenant un objet. Celui-ci permet de démarrer une caméra customisée & customisable en partie.

## Installation

    cordova plugin add https://github.com/ChristopheBoucaut/customCamera.git
    cordova build `platform`

## Plateformes supportées

+ Android

## Utilisation

### Commande

``` js
navigator.GeneanetCustomCamera.startCamera(options, onSuccess, onFail);
```

### Paramètres

#### *{Object}* options

L'objet `options` contient les options de configuration de l'appareil photo.

+ **imgBackgroundBase64 :** Image qui sera présente par dessus le rendu de l'appareil photo. Doit être en base64.
    - **Type :** `string`
    - **Valeur par défaut :** `null`

+ **miniature :** Permet d'activer ou non la fonction de miniature. `true` : Active l'option. `false` : Désactive l'option.
    - **Type :** `boolean`
    - **Valeur par défaut :** `true`

+ **saveInGallery :** Permet de stocker la photo dans la gallery du téléphone. `true` : Active l'option. `false` : Désactive l'option.
    - **Type :** `boolean`
    - **Valeur par défaut :** `false`

+ **cameraBackgroundColor :** Couleur pour le bouton de prise de photo.
    - **Type :** `string`
    - **Valeur par défaut :** `"#e26760"`
    - **Notes :**
        + Une mauvaise valeur ou une valeur `null` produit un effet de transparance.
        + Pour connaitre les formats de couleurs, consulter la méthode [`parseColor()`](http://developer.android.com/reference/android/graphics/Color.html#parseColor(java.lang.String)).

+ **cameraBackgroundColorPressed :** Couleur pour le bouton de prise de photo lorsqu'il est pressé.
    - **Type :** `string`
    - **Valeur par défaut :** `"#dc453d"`
    - **Notes :**
        + Une mauvaise valeur ou une valeur `null` produit un effet de transparance.
        + Pour connaitre les formats de couleurs, consulter la méthode [`parseColor()`](http://developer.android.com/reference/android/graphics/Color.html#parseColor(java.lang.String)).

+ **quality :** Qualité de la photo.
    - **Type :** `integer`
    - **Valeur par défaut :** `100`
    - **Notes :**
        + La valeur doit être comprise entre 0 et 100. Si la valeur n'est pas dans cet interval, la valeur par défaut est utilisée.
        + Pour plus d'information, consulter la méthode [`compress()`](http://developer.android.com/reference/android/graphics/Bitmap.html).

#### *{Function}* onSuccess

La fonction `onSuccess` est appelée lorsque la prise de vue est réussie.

+ **Paramètres :**
    - **result :**
        + **Type :** `string`
        + **Note :** Contient l'image prise au format base64.

#### *{Function}* onFail

La fonction `onFail` est appelée lorsque la prise de vue est ratée.
+ **Paramètres :**
    - **code :**
        + **Type :** `integer`
        + **Note :** Contient le code d'erreur.
            - **Code "2" :** Erreur lors de l'exécution de l'appareil photo.
            - **Code "3" :** L'utilisateur a fermé l'appareil photo sans prendre de photo.
    - **message :**
        + **Type :** `string`
        + **Note :** Contient un message détaillant l'erreur.

## Implémentation

### Exemple

``` js
var base64 = "...";
navigator.GeneanetCustomCamera.startCamera(
    {
        imgBackgroundBase64: base64,
        saveInGallery: true,
        miniature: false,
        quality: 70,
        cameraBackgroundColor: "#ffffff",
        cameraBackgroundColorPressed: null
    },
    function(result) {
        window.console.log("success");
        $("#imgTaken").attr("src", "data:image/jpeg;base64,"+result);
    },
    function(code, message) {
        window.console.log("fail");
        window.console.log(code);
        window.console.log(message);
    }
);
```

### AngularJS

Une implémentation dans AngularJS a été réalisée pour faciliter son utilisation : [TODO](TODO)

## Contribuer

Pour contribuer à ce projet, merci de respecter les règles suivantes :
+ **Les bugs, suggestions, etc :** Ils doivent être remontés via le système d'issues de Github. Merci de vérifier que votre sujet n'a pas déjà été traité.
+ **Développement Javascript :** Le code javascript doit être valide avec JSHint.
+ **Développement Java :** Le code java doit être valide [Checkstyle](http://eclipse-cs.sourceforge.net/#!/)