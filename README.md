# multiagente Src

Multi-agent system project source code, WEC 2018
Objective: to create a system in which several generic agents consult a central agent requesting a certain content. In this case, each agent
asks for a numeric data relative to a forecast.
The classes presented are based on the common libraries in Java and the Jade framework, the latter being responsible for the management and modeling
of the multi-agent system
--------------------------------------------------------------------------------------------------------------------

Codigo fonte de projecto de sistema multi-agente WEC 2018
Objectivo: criar um sistema em que vários agentes genéricos consultam um agente central solicitando um determinado conteúdo. Neste caso, cada agente
solicita um dado numérico relativo a uma previsão. 
As classes apresentadas baseiam-se nas bibliotecas comuns em Java e na framework Jade, sendo esta última a responsável pela gestão e modelação
do sistema multi-agente

Como replicar o funcionamento da aplicação:
* Importar as bibliotecas "libjade - commons-codec-1.3.jar" e "libjade - jade.jar"
* Em ambiente de depuração, usando o NetBeans:
   1. Project properties
   2. Run: Main class: jade.Boot
           Arguments: C:wec.central;B:wec.periodico;A:wec.periodico;
           
   Neste caso, ao executar a aplicação, serão criados um agente central de nome "C" e dois agentes genérico de nomes "A" e "B"
   

Classes:
* periodico.java: Implementa um modelo genérico, i.e. que só recebe informações do agente central. O nome deriva do seu comportamento que,
realiza de forma periodica, uma consulta ao agente central (um qualquer indicado pelas páginas amarelas que forneça o serviço).
* central.java: Implementa um modelo central, i.e. um agente que distribui uma informação que lhe seja pedida
* janela.java/janelaCentral.java: Interface gráfica dos agentes
* forecastData.java: Envelope de informação a trocar entre o interface gráfico e o agente em si, aplicando medidas de sincronização. O objecto
é também acedido por um daemon, ou seja, um processo que corre no fundo e transmite a informação por rede.
* client.java: Comunicação por socket entre agente central e processo de previsão da onda
* daemon.java: Comunicação por socket entre agente genérico e processo dinâmico MATLAB
* wec.java: main

Detalhes: Aplicação funcional com fins puramente académicos.
