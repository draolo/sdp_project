# sdp_project
Progetto realizzato per il corso di sistemi distribuiti e pervasivi.
## Descrizione del progetto
Lo scopo del progetto `e quello di realizzare un sistema per la gestione intelligente dell’energia elettrica prodotta da un complesso di case. Il consumo
elettrico di ogni casa viene misurato costantemente da uno smart meter
che monitora l’utilizzo degli elettrodomestici. L’architettura del sistema
da sviluppare è così descritta:  Il complesso di case costituisce una rete peer-to-peer dinamica volta alla gestione decentralizzata del consumo
elettrico. All’interno di questa rete, ogni Casa deve informare il vicinato
del suo consumo elettrico in tempo reale, in modo tale che ogni casa possa
calcolare il consumo energetico complessivo. Inoltre, quando una casa necessita di consumare una quantità di energia superiore alla norma, deve prima
chiedere il consenso alle altre case in modo tale da non superare la quota
complessiva prevista per il complesso. Le informazioni relative al consumo
di corrente condominiale vengono periodicamente fornite dalle case ad un
server remoto, chiamato Server amministratore, tramite il quale gli amministratori possono consultare i consumi energetici per calcolare le spese. Il
Server amministratore permette anche alle case di aggiungersi o rimuoversi
alla rete peer-to-peer del condominio in modo dinamico.
