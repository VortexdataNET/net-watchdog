<br />
<p align="center">
  <a href="https://github.com/othneildrew/Best-README-Template">
    <img src="https://github.com/TAXSET/net-watchdog/blob/master/media/icon-transparent.png?raw=true" alt="Logo" width="200" height="200">
  </a>

  <h3 align="center">NET Watchdog</h3>

  <p align="center">
    Periodically scan for service outages and react to them using webhooks.
    <br />
    <a href="https://github.com/othneildrew/Best-README-Template"><strong>Visit website »</strong></a>
    <br />
    <br />
    <a href="https://github.com/vortexdatanet/net-watchdog/issues">Report Bug</a>
    ·
    <a href="https://github.com/vortexdatanet/net-watchdog/issues">Request Feature</a>
  </p>
</p>



## Table of Contents

* [About](#about)
  * [Quick-Facts](#quick-facts)
  * [Libraries Used](#libraries-used)
* [Getting Started](#getting-started)
  * [Prerequisites](#prerequisites)
* [Components](#components)
  * [REST component](#rest-component)
  * [Socket component](#socket-component)
  * [Keys & values](#keys--values)
* [Main Config](#main-config)
* [Command Line](#command-line)
  * [Commands](#commands)
* [Usage](#usage)
  * [Creating and loading components](#creating-and-loading-components)
  * [Disabling a component](#disabling-a-component)
* [Contributing](#contributing)
* [License](#license)
* [Creator](#creator)
* [Contact](#contact)
* [Acknowledgements](#acknowledgements)



## About

The NET Watchdog was created in an effort to create a scalable, light-weight, self-sustaining system which checks for service outages or degradations. The application can then react to such events by calling webhooks which could for example notify system adminstrators, update a status page (eg. Cachet), send emails or trigger internal countermeasures.

### Quick Facts

* It's self hosted, you have more control
* Detailed logging and log rotation
* Multi-file component configuration
* Built with Java

### Libraries Used

* [Jansi](https://github.com/fusesource/jansi)
* [JLine3](https://github.com/jline/jline3)
* [org.json](https://github.com/stleary/JSON-java)
* [Logback](https://github.com/qos-ch/logback)
* [slf4j](https://github.com/qos-ch/slf4j)
* [junit](https://github.com/junit-team/junit5)



## Getting Started

Simply download the latest release and unzip it in a folder you want the application to build it's directory structure in. Once unzipped, simply run the jar files by using

```sh
java -jar net-watchdog.jar
```

> If you are on a Linux operating system you might want to use Screen to keep the application / session alive even after you exit your SSH session.

### Prerequisites

You have to have Java 8 installed.

For Linux users, you might want to install Screen.

```sh
apt-get install screen
```



## Components

The services you want to check are called components by the app. A component is defined and configured by a JSON config files in the `components` directory. The files name must end with `-component.conf` to be recognized by the app.

There are currently two types of components: REST and Socket components.

### REST component

A REST (representational state transfer) component can be used to check websites and REST API statuses. The JSON below shows an example configuration:

```json
{
   "type":"REST",
   "method":"GET",
   "name":"Example Service",
   "filename":"example-service",
   "address":"https://example.com/api/myapi.php",
   "performanceClasses":[
      {
         "name":"Operational",
         "responseTimeRange":"0-80",
         "webhookPosts":[
            {
               "address":"https://mystatuspage.com/api/v1/components/4",
               "headers":[
                  "Token:MySuperSecretToken"
               ],
               "body":"{\"status\":\"1\"}"
            }
         ]
      },
      {
         "name":"Performance Issues",
         "responseTimeRange":"81-1000",
         "webhookPosts":[
            {
               "address":"https://mystatuspage.com/api/v1/components/4",
               "headers":[
                  "Token:MySuperSecretToken"
               ],
               "body":"{\"status\":\"2\"}"
            }
         ]
      },
      {
         "name":"Partial Outage",
         "responseTimeRange":"1001-5000",
         "webhookPosts":[
            {
               "address":"https://mystatuspage.com/api/v1/components/4",
               "headers":[
                  "Token:MySuperSecretToken"
               ],
               "body":"{\"status\":\"3\"}"
            }
         ]
      },
      {
         "name":"Timeout",
         "responseTimeRange":"timeout",
         "webhookPosts":[
            {
               "address":"https://mystatuspage.com/api/v1/components/4",
               "headers":[
                  "Token:MySuperSecretToken"
               ],
               "body":"{\"status\":\"4\"}"
            }
         ]
      }
   ]
}
```

### Socket component

A socket component can connect to a server socket on a specific port (eg. heartbeat port on TSQPF).

```json
{
   "type":"SOCKET",
   "name":"My Database",
   "filename":"my-database",
   "address":"192.168.0.1",
   "port":"3306",
   "performanceClasses":[
      {
         "name":"Operational",
         "responseTimeRange":"0-80",
         "webhookPosts":[
            {
               "address":"https://mystatuspage.com/api/v1/components/4",
               "headers":[
                  "Token:MySuperSecretToken"
               ],
               "body":"{\"status\":\"1\"}"
            }
         ]
      },
      {
         "name":"Performance Issues",
         "responseTimeRange":"81-1000",
         "webhookPosts":[
            {
               "address":"https://mystatuspage.com/api/v1/components/4",
               "headers":[
                  "Token:MySuperSecretToken"
               ],
               "body":"{\"status\":\"2\"}"
            }
         ]
      },
      {
         "name":"Partial Outage",
         "responseTimeRange":"1001-5000",
         "webhookPosts":[
            {
               "address":"https://mystatuspage.com/api/v1/components/4",
               "headers":[
                  "Token:MySuperSecretToken"
               ],
               "body":"{\"status\":\"3\"}"
            }
         ]
      },
      {
         "name":"Timeout",
         "responseTimeRange":"timeout",
         "webhookPosts":[
            {
               "address":"https://mystatuspage.com/api/v1/components/4",
               "headers":[
                  "Token:MySuperSecretToken"
               ],
               "body":"{\"status\":\"4\"}"
            }
         ]
      }
   ]
}
```

### Keys & values

The tables below gives an overview of all root component and performance class keys and values, what they do and what data they can work with.

#### Root Keys

| Key                	| Value                                                          	| Fallback   	| Description                                          	                | Required 	| REST 	| SOCKET 	|
|--------------------	|----------------------------------------------------------------	|-------------  |--------------------------------------------------------------------	|----------	|------	|--------	|
| type               	| String, "REST" / "SOCKET"                                      	| N/A           | Component type.                                                      	| Yes      	| ✔️    	| ✔️      	|
| method             	| String, "GET" / "POST"                                         	| N/A           | Sets request method for REST component.                             	| Yes      	| ✔️    	| ❌      	    |
| name               	| String, any                                                    	| N/A           | Specifies the custom display name of the component.  	                | Yes      	| ✔️    	| ✔️      	|
| filename           	| String, must match conf. filename (excl. -component.conf part) 	| N/A           | Helps the app find the source component config file.              	| Yes      	| ✔️    	| ✔️      	|
| address            	| String, any                                                    	| N/A           | Defines the API / service address.                   	                | Yes      	| ✔️    	| ✔️      	|
| port              	| Integer, any                                                    	| 80            | Defines socket port.                                 	                | No      	| ❌️    	| ✔️      	|
| cacheLastResult    	| Boolean                                                         	| true          | True if webhooks should be re-run on same Performance Class result. 	| No       	| ✔️    	| ✔️      	|
| performanceClasses 	| Array, Performance Class configuration                         	| N/A           | Defines the Performance Classes used with component. 	                | No       	| ✔️    	| ✔️      	|

#### Performance Class

| Key               	| Value                                                         	| Def. Value 	| Description                                                                           	| Required 	| REST 	| SOCKET 	|
|-------------------	|---------------------------------------------------------------	|------------	|---------------------------------------------------------------------------------------	|----------	|------	|--------	|
| name              	| String, any                                                   	| N/A        	| Performance Class display name.                                                       	| Yes      	| ✔️    	| ✔️      	|
| responseTimeRange 	| String, two Integers devided by "-" or "timeout" for timeout. 	| N/A        	| Sets the response time range in milliseconds (eg. "10-80" or "timeout").              	| Yes      	| ✔️    	| ✔️      	|
| webhookPosts      	| Array, Webhooks                                               	| N/A        	| Sets the webhoks that are executed if component check returns this performance class. 	| No       	| ✔️    	| ✔️      	|
| contentLookup      	| String, any                                               	    | N/A        	| Lookup string in request response, overrides response time.                              	| No       	| ✔️    	| ✔️      	|

#### Webhook

| Key     	| Value         	| Def. Value 	| Description                                    	| Required 	| REST 	| SOCKET 	|
|---------	|---------------	|------------	|------------------------------------------------	|----------	|------	|--------	|
| address 	| String, any   	| N/A        	| Webhook address.                               	| Yes      	| ✔️    	| ✔️      	|
| headers 	| Array, String 	| N/A        	| Request headers, key and value devided by ":". 	| No       	| ✔️    	| ✔️      	|
| body    	| String, any   	| N/A        	| Request body.                                  	| No       	| ✔️    	| ✔️      	|

## Main Config

The main config just tells the app basic information like how long the delay between query cycles should be in seconds.

```json
{
   "pollRate": "30",
   "enabled": "true"
}
```



## Command Line

The command line gives administrators control over the app, what services it checks, and allows for creation and configuration of new components without having to exit the app, reducing downtime potential.

### Commands

| Name       	| Parameters                                                                  	| Description                                                	|
|------------	|-----------------------------------------------------------------------------	|------------------------------------------------------------	|
| exit       	| N/A                                                                         	| Stops the app.                                             	|
| clear      	| N/A                                                                         	| Clears CLI screen.                                         	|
| component  	| create [filename], disable [filename], enable [filename], delete [filename] 	| Manage and manipulate components.                          	|
| components 	| (reload)                                                                    	| Shows a list of all components or reloads all.             	|
| help       	| N/A                                                                         	| Shows a list of all commands, parameters and descriptions. 	|



## Usage

### Creating and loading components

Once you've successfully launched the app, you can use the command `component create <your-component-filename>` to create a base component template file. You'll need to edit the configs values and fill in your service details (see Components section).

When the component is fully configured, you can load it via the command `component enable <your-component-filename>`. The app will try to load and enable your component. You can ensure that your component is loaded by using the `components` command.

You can also reload and re-enable all component files by using the `components reload` command.

### Disabling a component

If you'd like to exclude a component from the query you can simply run the `component disabled <your-component-filename>` command. This will remove it from the loaded components pool.



## Contributing

Contributions are what make the open source community such an amazing place to be learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1. Fork the Project
2. Create your Feature Branch
3. Commit your Changes
4. Push to the Branch
5. Open a Pull Request



## Versioning

This project is versioned using the Semantic Versioning guidelines.



## License

This project is distributed under the MIT License. See `LICENSE` for more information.



## Creator

<h4>Sandro Kierner</h4>
- Twitter: https://twitter.com/OfficialTAXSET<br>
- GitHub: https://github.com/TAXSET<br>
- Email: sandro@vortexdata.net<br>




## Contact

This is a project published by VortexdataNET. If you have any questions, feel free to contact our support via support@vortexdata.net.



## Acknowledgements

* [Best README template by otheneildrew](https://github.com/othneildrew/Best-README-Template)
* [contributors-img](https://contributors-img.web.app/)





<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/othneildrew/Best-README-Template.svg?style=flat-square
[contributors-url]: https://github.com/othneildrew/Best-README-Template/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/othneildrew/Best-README-Template.svg?style=flat-square
[forks-url]: https://github.com/othneildrew/Best-README-Template/network/members
[stars-shield]: https://img.shields.io/github/stars/othneildrew/Best-README-Template.svg?style=flat-square
[stars-url]: https://github.com/othneildrew/Best-README-Template/stargazers
[issues-shield]: https://img.shields.io/github/issues/othneildrew/Best-README-Template.svg?style=flat-square
[issues-url]: https://github.com/othneildrew/Best-README-Template/issues
[license-shield]: https://img.shields.io/github/license/othneildrew/Best-README-Template.svg?style=flat-square
[license-url]: https://github.com/othneildrew/Best-README-Template/blob/master/LICENSE.txt
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=flat-square&logo=linkedin&colorB=555
[linkedin-url]: https://linkedin.com/in/othneildrew
[product-screenshot]: images/screenshot.png
