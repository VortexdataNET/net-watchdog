<br />
<p align="center">
  <a href="https://github.com/othneildrew/Best-README-Template">
    <img src="images/logo.png" alt="Logo" width="80" height="80">
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

* [About the Project](#about-the-project)
  * [Built With](#built-with)
* [Getting Started](#getting-started)
  * [Prerequisites](#prerequisites)
  * [Installation](#installation)
* [Usage](#usage)
* [Roadmap](#roadmap)
* [Contributing](#contributing)
* [License](#license)
* [Contact](#contact)



## About

The NET Watchdog was created in an effort to create a scalable, light-weight, self-sustaining system which checks for service outages or degradations. The application can then react to such events by calling webhooks which could for example message system adminstrators, update a status page (eg. Cachet), send emails or trigger internal countermeasures.

### Quick Facts

* It's self hosted, you have more control
* Detailed logging and log rotation
* Multi-file component configuration
* Built with Java

## Getting Started

Simply download the latest release and unzip it in a folder you want the application to build it's directory structure in. Once unzipped, simply run the jar files by using

```sh
java -jar net-watchdog.jar
```

> If you are on a Linux operating system you might want to use Screen to keep the application / session alive even after you exit your SSH session.

### Prerequisites

For all platforms, you have to have Java 8 installed.

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

```
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

## Main Config


<!-- USAGE EXAMPLES -->
## Usage

Use this space to show useful examples of how a project can be used. Additional screenshots, code examples and demos work well in this space. You may also link to more resources.

_For more examples, please refer to the [Documentation](https://example.com)_



<!-- ROADMAP -->
## Roadmap

See the [open issues](https://github.com/othneildrew/Best-README-Template/issues) for a list of proposed features (and known issues).



<!-- CONTRIBUTING -->
## Contributing

Contributions are what make the open source community such an amazing place to be learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request



<!-- LICENSE -->
## License

Distributed under the MIT License. See `LICENSE` for more information.



<!-- CONTACT -->
## Contact

Your Name - [@your_twitter](https://twitter.com/your_username) - email@example.com

Project Link: [https://github.com/your_username/repo_name](https://github.com/your_username/repo_name)



<!-- ACKNOWLEDGEMENTS -->
## Acknowledgements
* [GitHub Emoji Cheat Sheet](https://www.webpagefx.com/tools/emoji-cheat-sheet)
* [Img Shields](https://shields.io)
* [Choose an Open Source License](https://choosealicense.com)
* [GitHub Pages](https://pages.github.com)
* [Animate.css](https://daneden.github.io/animate.css)
* [Loaders.css](https://connoratherton.com/loaders)
* [Slick Carousel](https://kenwheeler.github.io/slick)
* [Smooth Scroll](https://github.com/cferdinandi/smooth-scroll)
* [Sticky Kit](http://leafo.net/sticky-kit)
* [JVectorMap](http://jvectormap.com)
* [Font Awesome](https://fontawesome.com)





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
