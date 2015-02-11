# org.geneanet.customcamera

Ce plugin cordova est une alternative au plugin cordova officiel (camera). Il permet de démarrer un appareil photo personnalisé (image en surimpression de l'appareil photo avec réglage de l'opacité, barre de zoom) et customisable (couleur des boutons, activation/désactivation de fonctions).

Ce plugin définit une variable globale `navigator.GeneanetCustomCamera`.

## Installation

    cordova plugin add https://github.com/geneanet/customCamera.git
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

+ **imgBackgroundBase64 :** Image qui sera présente en surimpression de l'appareil photo. Elle doit être en base64.
    - **Type :** `string`
    - **Valeur par défaut :** `null`

+ **imgBackgroundBase64OtherOrientation :** Image alternative qui sera présente en surimpression de l'appareil photo, pour le mode horizontal (ou vertical) selon l'orientation de démarrage du plugin. Elle doit être en base64. Si à `null`, on utilisera `imgBackgroundBase64` en redimensionnant l'image.
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

+ **opacity :** Permet d'activer ou non la fonction d'opacité de l'image en surimpression. `true` : Active l'option. `false` : Désactive l'option.
    - **Type :** `boolean`
    - **Valeur par défaut :** `true`

+ **defaultFlash :** Séléctionne un mode par défaut pour le flash. Voir `CustomCamera.FlashModes` pour les valeurs disponibles.
    - **Type :** `integer`
    - **Valeur par défaut :** `0`

+ **switchFlash :** Permet d'afficher ou non le bouton pour changer le mode du flash. `true` : Active l'option. `false` : Désactive l'option.
    - **Type :** `boolean`
    - **Valeur par défaut :** `true`

+ **defaultCamera :** Séléctionne un appareil photo (frontale/arrière) par défaut. Voir `CustomCamera.CameraFacings` pour les valeurs disponibles.
    - **Type :** `integer`
    - **Valeur par défaut :** `0`

+ **switchCamera :** Permet d'afficher ou non le bouton pour changer d'appareil photo. `true` : Active l'option. `false` : Désactive l'option.
    - **Type :** `boolean`
    - **Valeur par défaut :** `true`

#### *{Function}* onSuccess

La fonction `onSuccess` est appelée lorsque la prise de vue a réussie.

+ **Paramètres :**
    - **result :**
        + **Type :** `string`
        + **Note :** Contient l'image prise au format base64.

#### *{Function}* onFail

La fonction `onFail` est appelée lorsque la prise de vue a échouée.
+ **Paramètres :**
    - **code :**
        + **Type :** `integer`
        + **Note :** Contient le code d'erreur.
            - **Code "2" :** Erreur lors de l'exécution de l'appareil photo.
            - **Code "3" :** L'utilisateur a fermé l'appareil photo sans prendre de photo.
    - **message :**
        + **Type :** `string`
        + **Note :** Contient un message détaillant l'erreur.

## Constantes

+ **CustomCamera.FlashModes.DISABLE :**
    - **Type :** `integer`
    - **Valeur :** `0`
+ **CustomCamera.FlashModes.ACTIVE :**
    - **Type :** `integer`
    - **Valeur :** `1`
+ **CustomCamera.FlashModes.AUTO :**
    - **Type :** `integer`
    - **Valeur :** `2`

+ **CustomCamera.CameraFacings.BACK :**
    - **Type :** `integer`
    - **Valeur :** `0`
+ **CustomCamera.CameraFacings.FRONT :**
    - **Type :** `integer`
    - **Valeur :** `1`

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

### Application de code barre

[Voir le code](https://github.com/geneanet/customCamera/tree/master/examples/barcode)

![Barcode](https://raw.githubusercontent.com/geneanet/customCamera/master/examples/barcode/screenshot.png)

### Application avec grille

[Voir le code](https://github.com/geneanet/customCamera/tree/master/examples/grid)

![Grid](https://raw.githubusercontent.com/geneanet/customCamera/master/examples/grid/screenshot.png)

### AngularJS

Une implémentation dans AngularJS a été réalisée pour faciliter son utilisation : [$geneanetCustomCamera](https://github.com/geneanet/customCameraAngular.git).

## Contribuer

Pour contribuer à ce projet, merci de respecter les règles suivantes :
+ **Les bugs, suggestions, etc :** Ils doivent être remontés via le système d'issues de Github. Merci de vérifier que votre sujet n'a pas déjà été traité.
+ **Développement Javascript :** Le code javascript doit être valide avec JSHint.
