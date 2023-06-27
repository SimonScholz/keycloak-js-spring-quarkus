<template>
  <img alt="Vue logo" src="./assets/logo.png" />
  <button @click="keycloak.login()">Login</button>
  <button @click="keycloak.login({ action: 'UPDATE_PASSWORD' })">
    Update Password
  </button>
  <button @click="keycloak.logout">Logout</button>
  <button @click="keycloak.register">Register</button>
  <button @click="keycloak.accountManagement">Account</button>
  <button @click="refreshToken(9999)">Refresh Token</button>
  <button @click="refreshToken(30)">Refresh Token (if 30s validity)</button>
  <button @click="loadProfile">Get Profile</button>
  <button @click="updateProfile">Update profile</button>
  <button @click="loadUserInfo">Get User Info</button>
  <button @click="output(keycloak.tokenParsed)">Show Token</button>
  <button @click="output(keycloak.token)">Show Token (raw)</button>
  <button @click="output(keycloak.refreshTokenParsed)">
    Show Refresh Token
  </button>
  <button @click="output(keycloak.idTokenParsed)">Show ID Token</button>
  <button @click="showExpires()">Show Expires</button>
  <button @click="output(keycloak)">Show Details</button>
  <button @click="output(keycloak.createLoginUrl())">Show Login URL</button>
  <button @click="output(keycloak.createLogoutUrl())">Show Logout URL</button>
  <button @click="output(keycloak.createRegisterUrl())">
    Show Register URL
  </button>
  <button @click="output(keycloak.createAccountUrl())">Show Account URL</button>

  <h2>Result</h2>
  <pre
    style="
      background-color: #ddd;
      border: 1px solid #ccc;
      padding: 10px;
      word-wrap: break-word;
      white-space: pre-wrap;
    "
    id="output"
  ></pre>

  <h2>Events</h2>
  <pre
    style="
      background-color: #ddd;
      border: 1px solid #ccc;
      padding: 10px;
      word-wrap: break-word;
      white-space: pre-wrap;
    "
    id="events"
  ></pre>
</template>

<script setup lang="ts">
import Keycloak, { KeycloakInitOptions } from "keycloak-js";

const keycloak = new Keycloak();

keycloak.onAuthSuccess = function () {
  event("Auth Success");
};

keycloak.onAuthError = function (errorData: any) {
  event("Auth Error: " + JSON.stringify(errorData));
};

keycloak.onAuthRefreshSuccess = function () {
  event("Auth Refresh Success");
};

keycloak.onAuthRefreshError = function () {
  event("Auth Refresh Error");
};

keycloak.onAuthLogout = function () {
  event("Auth Logout");
};

keycloak.onTokenExpired = function () {
  event("Access token expired.");
};

keycloak.onActionUpdate = function (status: string) {
  switch (status) {
    case "success":
      event("Action completed successfully");
      break;
    case "cancelled":
      event("Action cancelled by user");
      break;
    case "error":
      event("Action failed");
      break;
  }
};
// Flow can be changed to 'implicit' or 'hybrid', but then client must enable implicit flow in admin console too
const initOptions: KeycloakInitOptions = {
  responseMode: "fragment",
  flow: "standard",
};

keycloak
  .init(initOptions)
  .then(function (authenticated) {
    output(
      "Init Success (" +
        (authenticated ? "Authenticated" : "Not Authenticated") +
        ")"
    );
  })
  .catch(function () {
    output("Init Error");
  });

function login() {
  keycloak.login();
}

function logout() {
  keycloak.logout();
}

function loadProfile() {
  keycloak
    .loadUserProfile()
    .then(function (profile) {
      output(profile);
    })
    .catch(function () {
      output("Failed to load profile");
    });
}

function loadUserInfo() {
  keycloak
    .loadUserInfo()
    .then(function (userInfo) {
      output(userInfo);
    })
    .catch(function () {
      output("Failed to load user info");
    });
}

function refreshToken(minValidity: any) {
  keycloak
    .updateToken(minValidity)
    .then(function (refreshed) {
      if (refreshed) {
        output(keycloak.tokenParsed);
      } else {
        output(
          "Token not refreshed, valid for " +
            Math.round(
              keycloak.tokenParsed.exp +
                keycloak.timeSkew -
                new Date().getTime() / 1000
            ) +
            " seconds"
        );
      }
    })
    .catch(function () {
      output("Failed to refresh token");
    });
}

function showExpires() {
  if (!keycloak.tokenParsed) {
    output("Not authenticated");
    return;
  }

  var o =
    "Token Expires:\t\t" +
    new Date(
      (keycloak.tokenParsed.exp + keycloak.timeSkew) * 1000
    ).toLocaleString() +
    "\n";
  o +=
    "Token Expires in:\t" +
    Math.round(
      keycloak.tokenParsed.exp + keycloak.timeSkew - new Date().getTime() / 1000
    ) +
    " seconds\n";

  if (keycloak.refreshTokenParsed) {
    o +=
      "Refresh Token Expires:\t" +
      new Date(
        (keycloak.refreshTokenParsed.exp + keycloak.timeSkew) * 1000
      ).toLocaleString() +
      "\n";
    o +=
      "Refresh Expires in:\t" +
      Math.round(
        keycloak.refreshTokenParsed.exp +
          keycloak.timeSkew -
          new Date().getTime() / 1000
      ) +
      " seconds";
  }

  output(o);
}

function updateProfile() {
  var url = keycloak.createAccountUrl().split("?")[0];
  var req = new XMLHttpRequest();
  req.open("POST", url, true);
  req.setRequestHeader("Accept", "application/json");
  req.setRequestHeader("Content-Type", "application/json");
  req.setRequestHeader("Authorization", "bearer " + keycloak.token);

  req.onreadystatechange = function () {
    if (req.readyState == 4) {
      if (req.status == 200) {
        output("Success");
      } else {
        output("Failed");
      }
    }
  };

  req.send('{"email":"myemail@foo.bar","firstName":"test","lastName":"bar"}');
}

function output(data: any) {
  if (typeof data === "object") {
    data = JSON.stringify(data, null, "  ");
  }
  document.getElementById("output").innerHTML = data;
}

function event(event: any) {
  var e = document.getElementById("events").innerHTML;
  document.getElementById("events").innerHTML =
    new Date().toLocaleString() + "\t" + event + "\n" + e;
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
