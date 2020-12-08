<br />
<p align="center">
  <a href="https://netwatchdog.vortexdata.net">
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
* [Configs](#configs)
  * [Main Config](#main-config)
  * [Northstar Config](#northstar-config)
* [Command Line](#command-line)
  * [Commands](#commands)
* [Launch Parameters](#launch-parameters)
* [Usage](#usage)
  * [Creating and loading components](#creating-and-loading-components)
  * [Disabling a component](#disabling-a-component)
* [Contributing](#contributing)
* [License](#license)
* [Creator](#creator)
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

## Configs

### Main Config

The main config just tells the app basic information like how long the delay between query cycles should be in seconds.

```json
{
   "pollDelay": "30",
   "enableNorthstars": "true"
}
```

| Key     	        | Value         	| Def. Value 	| Description                                    	     |
|------------------	|---------------	|------------	|----------------------------------------------------- |
| pollDelay	        | Int, x > 0   	  | N/A        	| Delay of component scan cycles in seconds.     	     |
| enableNorthstars 	| Boolean        	| N/A        	| Specifies if Northstar system should be initiated.   |

### Northstar Config

In the Northstar config (northstar.conf) specifies which addresses and/or sockets are used as Northstar refference points.

```json
{
   "availPercentMin": "100",
   "northstars": [
      {
         "address": "1.1.1.1",
         "type": "ICMP"
      },
      {
         "address": "8.8.8.8",
         "type": "SOCKET",
         "port": "80"
      }
   ]
}
```

| Key     	        | Value         	            | Def. Value 	| Description                                    	                                                             |
|------------------	|---------------------------	|------------	|------------------------------------------------------------------------------------------------------------- |
| availPercentMin   | Int, x >= 0 & x <= 100   	  | N/A        	| Minimum required percentage of refference points that have to be reached for check cycle to start.     	     |
| northstars       	| Array of Northstars         | N/A        	| Specifies Northstar refference points.                                                                       |

## Command Line

The command line gives administrators control over the app, what services it checks, and allows for creation and manipulation of components at runtime.

### Commands

| Name       	| Parameters                                                                  	| Description                                                	|
|------------	|-----------------------------------------------------------------------------	|------------------------------------------------------------	|
| exit       	| N/A                                                                         	| Stops the app.                                             	|
| clear      	| N/A                                                                         	| Clears CLI screen.                                         	|
| component  	| create [filename], disable [filename], enable [filename], delete [filename] 	| Manage and manipulate components.                          	|
| components 	| (reload)                                                                    	| Shows a list of all components or reloads all.             	|
| help       	| N/A                                                                         	| Shows a list of all commands, parameters and descriptions. 	|


## Launch Parameters

You can use a set of launch parameters to start the app in spezial modi.

### Usage

There are two forms of a launch parameter: Short and long form. The short form has a single "-" as prefix when used while the long form has "--".

Simply append the launch parameters after the jar name as shown in the examples below.

Setting ignore critical config using long form:
```bash
java -jar net-watchdog.jar --ignoreCriticalConfig
```

Setting log level using long form:
```bash
java -jar net-watchdog.jar --logLevel DEBUG
```

Setting log level using short form:
```bash
java -jar net-watchdog.jar -lL DEBUG
```

Setting log level and ignore critical config using short form:
```bash
java -jar net-watchdog.jar -lL DEBUG -iCC
```

### Available Parameters

| Short | Long                 | Options                                     | Description                                 |
|-------|----------------------|---------------------------------------------|---------------------------------------------|
| iCC   | ignoreCriticalConfig | N/A                                         | Any errors during config check are ignored. |
| ll    | logLevel             | String, "DEBUG" / "INFO" / "WARN" / "ERROR" | Sets the log level.                         |


## Usage

### Creating and loading components

Once you've successfully launched the app, you can use the command `component create <your-component-filename>` to create a base component template file. You'll need to edit the configs values and fill in your service details (see Components section).

When the component is fully configured, you can load it via the command `component enable <your-component-filename>`. The app will try to load and enable your component. You can check if your component is loaded by using the `components` command.

You can also reload and re-enable all component files by using the `components reload` command.

### Disabling a component

If you'd like to exclude a component from the query you can simply run the `component disable <your-component-filename>` command. This will remove it from the loaded components pool.



## Contributing

Contributions are what make the open source community such an amazing place to be learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1. Fork the Project
2. Create your Feature Branch
3. Commit your Changes
4. Push to the Branch
5. Open a Pull Request



## Versioning

This project is versioned using the Semantic Versioning 2.0 guidelines.



## License

This project is distributed under the MIT License. See `LICENSE` for more information.



## Authors

<h4>Sandro Kierner</h4>
- Twitter: https://twitter.com/OfficialTAXSET<br>
- GitHub: https://github.com/TAXSET<br>
- Email: sandro@vortexdata.net<br>

<h4>Michael Wiesinger</h4>
- Twitter: https://twitter.com/Varchar42<br>
- GitHub: https://github.com/Varchar42<br>
- Email: michael@vortexdata.net<br>


## Acknowledgements

* [Best README template by otheneildrew](https://github.com/othneildrew/Best-README-Template)
* [contributors-img](https://contributors-img.web.app/)
